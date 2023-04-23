/*
 * Este código fuente es Confidencial y también puede contener información privilegiada, es para uso exclusivo de E-Global.
 * Tenga en cuenta que cualquier distribución, copia o uso de esta información sin autorización está estrictamente prohibida.
 * Si ha identificado algún mal uso de este código fuente por favor notifiquelo a la dirección de correo seginfo@eglobal.com.mx
 */
package com.eglobal.genera.resumen.reporte.b.constructor;

import com.eglobal.beans.ConsultaSimpleResponseData;
import com.eglobal.reporte.b.commons.beans.EmisoresConsultaBean;
import com.eglobal.genera.resumen.reporte.b.beans.RegistroConsultaCaratulas;
import com.eglobal.genera.resumen.reporte.b.beans.RegistroConsultaInformix;
import com.eglobal.genera.resumen.reporte.b.beans.RegistroXAdquirente;
import com.eglobal.genera.resumen.reporte.b.beans.RegistroXMarca;
import com.eglobal.genera.resumen.reporte.b.beans.dao.Dao;
import com.eglobal.genera.resumen.reporte.b.entities.ResumenReporteB;
import com.eglobal.reporte.b.commons.beans.exceptions.GeneraResumenReporteBException;
import com.eglobal.reporte.b.commons.providers.ProveedorBinesBUB;
import com.eglobal.reporte.b.commons.providers.ProveedorProperties;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author egldt1134
 */
public abstract class ConstructorResumenAbstracto {

    private static final Logger LOG = LogManager.getLogger(ConstructorResumenAbstracto.class);

    protected Dao dao;
    protected EntityManager emI;
    protected EntityManager emP;
    
    private Map<Integer,RegistroConsultaInformix> mTotal;
    private Map<Integer,Map<Integer,RegistroXMarca>> mPorMarca;
    private Map<Integer,Map<Integer,Map<Integer,RegistroXAdquirente>>> mPorMarcaPorAdq;
    
    public ConstructorResumenAbstracto(Dao dao) {
        this.dao = dao;

        ProveedorProperties pp = ProveedorProperties.GETINSTANCE();

        EntityManagerFactory emfI;
        EntityManagerFactory emfP;
        try {
            emfI = Persistence.createEntityManagerFactory("informix_PU", pp.getPropsI());
            emfP = Persistence.createEntityManagerFactory("postgreSQL_PU", pp.getPropsP());
            emI = emfI.createEntityManager();
            emP = emfP.createEntityManager();
        } catch (IOException ex) {
            LOG.error("No fue posible crear el manejador de entidades. ", ex);
            System.exit(-1);
        } 
    }

    public void construirResumenes(String fecha) {

        Map<Integer, List<EmisoresConsultaBean>> mapaEmisores;
        List<RegistroConsultaInformix> lstRegistroInformix = new LinkedList<>();
        Connection connection = null;
        long inicio,fin;
        try {
            
            LOG.info("Se eliminarán registros existentes para la fecha " 
                    + fecha + " del proceso " + this.obtenerIdProceso() + "...");
            this.eliminarRegistros(fecha);
            LOG.info("Finaliza proceso de borrado.");
            
            mapaEmisores = this.leerEmisores();
            emI.getTransaction().begin();
            connection = emI.unwrap(Connection.class);
            PreparedStatement statement;
            for (Integer key : mapaEmisores.keySet()) {
                List<EmisoresConsultaBean> listEc = mapaEmisores.get(key);
                LOG.info("Inicia ejecución de consultas emisor: " + key);
                inicio = System.currentTimeMillis();
                for (EmisoresConsultaBean ecb : listEc) {
                    statement = connection.prepareStatement(ecb.getQuery());
                    statement.setString(1, fecha);
                    
                    ResultSet rs = dao.executeSelectingSql(statement);
                    
                    while (rs.next()) {
                        RegistroConsultaInformix rci = new RegistroConsultaInformix();
                        rci.setFechaProceso(rs.getDate(1));
                        rci.setIdEmisor(rs.getInt(2));
                        rci.setTotalTransacciones(rs.getInt(3));
                        rci.setMontoTransacciones(new BigDecimal(rs.getString(4) != null ? rs.getString(4) : "0.00"));
                        rci.setMarca(rs.getInt(5));
                        rci.setIdAdquirente(rs.getInt(6));
                        rci.setIdCarat(ecb.getIdCaratula());
                        if(ecb.isSubAfiliados()){
                            rci.setBin(rs.getString(7));
                            rci.setSubafiliados(true);
                        }
                        lstRegistroInformix.add(rci);
                    }
                }
                fin = System.currentTimeMillis();
                LOG.info("Finaliza ejecución de consultas emisor: " + key);
                LOG.info("Tiempo de Ejecución ms: " + (fin-inicio));
            }
        } catch (GeneraResumenReporteBException ex) {
            LOG.info(ex.getMessage());
            System.exit(-1);
        } catch (SQLException ex) {
            LOG.info("Ha ocurrido un error en Base de Datos",ex);
            System.exit(-1);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    LOG.info("Ocurrió un error al cerrar la conexión.", ex);
                }
            }
            if (this.emI.isOpen()) {
                this.emI.close();
            }
        }
        
        this.mTotal = this.generarListTotal(lstRegistroInformix);
        this.mPorMarca = this.generarListaMarcas(lstRegistroInformix);
        this.mPorMarcaPorAdq = this.generarListaMarcasAdquirentes(lstRegistroInformix);
        
        this.guardarRegistros(fecha);
        
    }

    private Map<Integer,RegistroConsultaInformix> generarListTotal(List<RegistroConsultaInformix> list){
        Map<Integer,RegistroConsultaInformix> mapa = new LinkedHashMap<>();
        for(RegistroConsultaInformix r : list){
            String binS = r.getBin()!=null ? r.getBin() : "";
            ConsultaSimpleResponseData cSrD = ProveedorBinesBUB.GETINSTANCE().getMapaBinesEv().get(binS);
            
            Integer idEmisor = cSrD != null ? new Integer(r.getBin()) : r.getIdEmisor();
            
            RegistroConsultaInformix rci = mapa.get(idEmisor);
            if(rci == null){
                rci = new RegistroConsultaInformix();
                rci.setFechaProceso(r.getFechaProceso());
                rci.setIdAdquirente(r.getIdAdquirente());
                rci.setIdEmisor(r.getIdEmisor());
                rci.setMarca(r.getMarca());
                rci.setTotalTransacciones(r.getTotalTransacciones());
                rci.setMontoTransacciones(r.getMontoTransacciones());
                rci.setSubafiliados(r.isSubafiliados());
                rci.setBin(cSrD != null ? binS : null);
                rci.setIdCarat(r.getIdCarat());
                mapa.put(idEmisor, rci);
            }else{
                rci.setTotalTransacciones(rci.getTotalTransacciones()+r.getTotalTransacciones());
                rci.setMontoTransacciones(rci.getMontoTransacciones().add(r.getMontoTransacciones()));
            }
        }    
        return mapa;
    }
    
    private Map<Integer,Map<Integer,RegistroXMarca>> generarListaMarcas(List<RegistroConsultaInformix> list){
        Map<Integer,Map<Integer,RegistroXMarca>> mapaCMarca = new LinkedHashMap<>();
        for(RegistroConsultaInformix r : list){
            String binS = r.getBin()!=null ? r.getBin() : "";
            ConsultaSimpleResponseData cSrD = ProveedorBinesBUB.GETINSTANCE().getMapaBinesEv().get(binS);
            
            Integer idEmisor = cSrD != null ? new Integer(r.getBin()) : r.getIdEmisor();
            
            Map<Integer,RegistroXMarca> mrpm = mapaCMarca.get(idEmisor);
            RegistroXMarca rxm;
            if(mrpm == null){
                mrpm = new LinkedHashMap<>();
                rxm = new RegistroXMarca();
                rxm.setIdMarca(r.getMarca());
                rxm.setNumeroTxn(r.getTotalTransacciones());
                rxm.setMontoTotalTxn(r.getMontoTransacciones());
                mrpm.put(r.getMarca(), rxm);
                mapaCMarca.put(idEmisor, mrpm);
            }else{
                rxm = mrpm.get(r.getMarca());
                if (rxm == null) {
                    rxm = new RegistroXMarca();
                    rxm.setIdMarca(r.getMarca());
                    rxm.setNumeroTxn(r.getTotalTransacciones());
                    rxm.setMontoTotalTxn(r.getMontoTransacciones());
                    mrpm.put(r.getMarca(), rxm);
                }else{
                    rxm.setNumeroTxn(rxm.getNumeroTxn()+r.getTotalTransacciones());
                    rxm.setMontoTotalTxn(rxm.getMontoTotalTxn().add(r.getMontoTransacciones()));
                }
            }
        }
        return mapaCMarca;
    }
    private Map<Integer,Map<Integer,Map<Integer,RegistroXAdquirente>>> generarListaMarcasAdquirentes(List<RegistroConsultaInformix> list){
        Map<Integer,Map<Integer,Map<Integer,RegistroXAdquirente>>> mapaMarcaXAdq = new LinkedHashMap<>();
        for(RegistroConsultaInformix r : list){
            String binS = r.getBin()!=null ? r.getBin() : "";
            ConsultaSimpleResponseData cSrD = ProveedorBinesBUB.GETINSTANCE().getMapaBinesEv().get(binS);
            
            Integer idEmisor = cSrD != null ? new Integer(r.getBin()) : r.getIdEmisor();
            Map<Integer,Map<Integer,RegistroXAdquirente>> m1 = mapaMarcaXAdq.get(idEmisor);
            RegistroXAdquirente rxAdq;
            Map<Integer,RegistroXAdquirente> mapRegxAdq;
            if(m1 == null){
                m1 = new LinkedHashMap<>();
                mapRegxAdq = new LinkedHashMap<>();
                rxAdq = new RegistroXAdquirente();
                rxAdq.setIdAdquirente(r.getIdAdquirente());
                rxAdq.setTotalTxn(r.getTotalTransacciones());
                rxAdq.setMontoTotalTxn(r.getMontoTransacciones());
                mapRegxAdq.put(r.getIdAdquirente(), rxAdq);
                m1.put(r.getMarca(), mapRegxAdq);
                mapaMarcaXAdq.put(idEmisor,m1);
            }else{
                mapRegxAdq = m1.get(r.getMarca());
                if(mapRegxAdq==null){
                    mapRegxAdq = new LinkedHashMap<>();
                    rxAdq = new RegistroXAdquirente();
                    rxAdq.setIdAdquirente(r.getIdAdquirente());
                    rxAdq.setTotalTxn(r.getTotalTransacciones());
                    rxAdq.setMontoTotalTxn(r.getMontoTransacciones());
                    mapRegxAdq.put(r.getIdAdquirente(), rxAdq);
                    mapaMarcaXAdq.get(idEmisor).put(r.getMarca(), mapRegxAdq);
                }else{
                    rxAdq = mapRegxAdq.get(r.getIdAdquirente());
                    if(rxAdq==null){
                        rxAdq = new RegistroXAdquirente();
                        rxAdq.setIdAdquirente(r.getIdAdquirente());
                        rxAdq.setTotalTxn(r.getTotalTransacciones());
                        rxAdq.setMontoTotalTxn(r.getMontoTransacciones());
                        mapRegxAdq.put(r.getIdAdquirente(), rxAdq);
                    }else{
                        rxAdq.setTotalTxn(rxAdq.getTotalTxn()+r.getTotalTransacciones());
                        rxAdq.setMontoTotalTxn(rxAdq.getMontoTotalTxn().add(r.getMontoTransacciones()));
                    }
                }
            }
        }
        return mapaMarcaXAdq;
    }
    
    public void eliminarRegistros(String fecha) {
        ProveedorProperties pp = ProveedorProperties.GETINSTANCE();
        Properties props = null;
        try {
            props = pp.getPropsP();
        } catch (IOException ex) {
            LOG.info("Ocurrió un error al abrir o cerrar archivo de porpiedades. ");
            System.exit(-1);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date f = null;
        try {
            f = sdf.parse(fecha);
        } catch (ParseException ex) {
            LOG.info("Ocurrió un error al parser la fecha " + fecha);
            System.exit(-1);
        }

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("postgreSQL_PU", props);
        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();

        TypedQuery<ResumenReporteB> query = em.createNamedQuery("ResumenReporteB.findByFechaProcesoAndProceso", ResumenReporteB.class);
        query.setParameter("fechaProceso", f);
        query.setParameter("idProceso", this.obtenerIdProceso());

        List<ResumenReporteB> resumenes = query.getResultList();
        
        LOG.info("Registros a eliminar: " + (resumenes != null ? resumenes.size() : 0));
        
        for (ResumenReporteB r : resumenes) {
            em.remove(r);
        }

        em.getTransaction().commit();
    }
    
    public abstract Map<Integer, List<EmisoresConsultaBean>> leerEmisores() throws GeneraResumenReporteBException;
    public abstract void guardarRegistros(String fecha);
    public abstract RegistroConsultaCaratulas obtenerDatosCaratula(String fecha, String id);
    public abstract Integer obtenerIdProceso();
    
    public Map<Integer, RegistroConsultaInformix> getmTotal() {
        return mTotal;
    }

    public Map<Integer, Map<Integer, RegistroXMarca>> getmPorMarca() {
        return mPorMarca;
    }

    public Map<Integer, Map<Integer, Map<Integer, RegistroXAdquirente>>> getmPorMarcaPorAdq() {
        return mPorMarcaPorAdq;
    } 
}
