/*
 * Este código fuente es Confidencial y también puede contener información privilegiada, es para uso exclusivo de E-Global.
 * Tenga en cuenta que cualquier distribución, copia o uso de esta información sin autorización está estrictamente prohibida.
 * Si ha identificado algún mal uso de este código fuente por favor notifiquelo a la dirección de correo seginfo@eglobal.com.mx
 */
package com.eglobal.genera.resumen.reporte.b.constructor;

import com.eglobal.beans.InstitucionesResponseData;
import com.eglobal.reporte.b.commons.beans.EmisoresConsultaBean;
import com.eglobal.genera.resumen.reporte.b.beans.RegistroConsultaCaratulas;
import com.eglobal.genera.resumen.reporte.b.beans.RegistroConsultaInformix;
import com.eglobal.genera.resumen.reporte.b.beans.RegistroXAdquirente;
import com.eglobal.genera.resumen.reporte.b.beans.RegistroXMarca;
import com.eglobal.genera.resumen.reporte.b.beans.dao.Dao;
import com.eglobal.genera.resumen.reporte.b.entities.CMarcas;
import com.eglobal.genera.resumen.reporte.b.entities.CProcesos;
import com.eglobal.genera.resumen.reporte.b.entities.ResumenReporteB;
import com.eglobal.genera.resumen.reporte.b.entities.ResumenReporteBXAdquirente;
import com.eglobal.genera.resumen.reporte.b.entities.ResumenReporteBXAdquirentePK;
import com.eglobal.genera.resumen.reporte.b.entities.ResumenReporteBXMarcas;
import com.eglobal.reporte.b.commons.beans.exceptions.GeneraResumenReporteBException;
import com.eglobal.reporte.b.commons.beans.insumos.impl.LeerEmisoresConsultasPOS;
import com.eglobal.reporte.b.commons.providers.ProveedorBinesBUB;
import com.eglobal.reporte.b.commons.providers.ProveedorInstitucionesBUB;
import com.eglobal.reporte.b.commons.providers.ProveedorProperties;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author egldt1134
 */
public class ConstructorResumenPOS extends ConstructorResumenAbstracto {

    private static final Logger LOG = LogManager.getLogger(ConstructorResumenPOS.class);

    public ConstructorResumenPOS(Dao dao) {
        super(dao);
    }

    @Override
    public Map<Integer, List<EmisoresConsultaBean>> leerEmisores() throws GeneraResumenReporteBException {
        LeerEmisoresConsultasPOS lecp = new LeerEmisoresConsultasPOS();
        Map<Integer, List<EmisoresConsultaBean>> mapaEmisores = lecp.obtenerEmisores("consultasEmisoresPOSProperties.xml");
        return mapaEmisores;
    }

    @Override
    public void guardarRegistros(String fecha) {
        
        ProveedorProperties pp = ProveedorProperties.GETINSTANCE();
        Properties props = null;
        try {
            props = pp.getPropsP();
        } catch (IOException ex) {
            LOG.info("Ocurrió un error al abrir o cerrar archivo de porpiedades. ");
            System.exit(-1);
        }
        
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("postgreSQL_PU", props);
        EntityManager em = emf.createEntityManager();
        
        em.getTransaction().begin();
        Map<Integer, RegistroConsultaInformix> mTotal = this.getmTotal();
        Map<Integer, Map<Integer, RegistroXMarca>> mPorMarca = this.getmPorMarca();
        Map<Integer, Map<Integer, Map<Integer, RegistroXAdquirente>>> mPorMarcaPorAdq = this.getmPorMarcaPorAdq();

        LOG.info("Se guardarán un total de " + mTotal.keySet().size() + " Emisores");
        Collection<Collection<ResumenReporteBXAdquirente>> listaDeListaAdq = new LinkedList<>();
        LOG.info("Inicia ciclo de guardado de Resumen: Total y por marcas");
        for (Integer idEmisor : mTotal.keySet()) {
            RegistroConsultaInformix registroConsultaInformix = mTotal.get(idEmisor);
            LOG.info("Inicia consulta de carátulas del emisor " + registroConsultaInformix.getIdCarat());
            RegistroConsultaCaratulas rcc = this.obtenerDatosCaratula(fecha, registroConsultaInformix.getIdCarat());
            LOG.info("Finaliza consulta de carátulas del emisor " + registroConsultaInformix.getIdCarat());
            ResumenReporteB resumenReporteB = new ResumenReporteB();
            resumenReporteB.setFechaProceso(registroConsultaInformix.getFechaProceso());
            resumenReporteB.setIdEmisor(registroConsultaInformix.getIdEmisor());
            Map<Integer, InstitucionesResponseData> map = ProveedorInstitucionesBUB.GETINSTANCE().getMapaInstitucionesEmi();
            
            resumenReporteB.setDescEmisor(map.get(registroConsultaInformix.getIdEmisor())!=null ?  map.get(registroConsultaInformix.getIdEmisor()).getNombreInstitucion() : "NOT FOUND");
            resumenReporteB.setIdInstitucionBm(
                    registroConsultaInformix.getBin() != null ? new Integer(registroConsultaInformix.getBin())
                    : registroConsultaInformix.getIdEmisor());
            String nInstB = map.get(idEmisor) != null ? map.get(idEmisor).getNombreInstitucion() : "NOT FOUND";
            resumenReporteB.setDescIntitucionBm(registroConsultaInformix.getBin() != null
                    ? ProveedorBinesBUB.GETINSTANCE().getMapaBinesEv().get(registroConsultaInformix.getBin())
                            .getNombreInstitucion()
                    : nInstB);
            resumenReporteB.setTotalTxnInt(registroConsultaInformix.getTotalTransacciones());
            resumenReporteB.setMontoTotalTxnInt(registroConsultaInformix.getMontoTransacciones());
            resumenReporteB.setDisponible(true);
            resumenReporteB.setSubafiliado(registroConsultaInformix.isSubafiliados());
            resumenReporteB.setIdProceso(em.getReference(CProcesos.class, 1));
            resumenReporteB.setTotalTxnCaratula(rcc.getTotalTxn());
            resumenReporteB.setMontoTotalTxnCaratula(rcc.getMontoTotalTxn());
            resumenReporteB.setTxnIntVsCarat(resumenReporteB.getTotalTxnCaratula()-resumenReporteB.getTotalTxnInt());
            resumenReporteB.setMontoIntVsCarat(resumenReporteB.getMontoTotalTxnCaratula().subtract(resumenReporteB.getMontoTotalTxnInt()));
            Map<Integer, RegistroXMarca> mapaRegxMarca = mPorMarca.get(idEmisor);
            Collection<ResumenReporteBXMarcas> lstResumenReporteBXMarcas = new ArrayList<>();

            for (Integer marca : mapaRegxMarca.keySet()) {
                ResumenReporteBXMarcas rRbXM = new ResumenReporteBXMarcas();
                rRbXM.setIdMarca(em.getReference(CMarcas.class, marca));
                rRbXM.setTotalTxnInt(mapaRegxMarca.get(marca).getNumeroTxn());
                rRbXM.setMontoTotalTxnInt(mapaRegxMarca.get(marca).getMontoTotalTxn());
                rRbXM.setIdRegistro(resumenReporteB);

                Map<Integer, RegistroXAdquirente> mapaRegxAdq = mPorMarcaPorAdq.get(idEmisor).get(marca);
                Collection<ResumenReporteBXAdquirente> lstResumenReporteBXAdquirente = new ArrayList<>();
                for (Integer adq : mapaRegxAdq.keySet()) {
                    ResumenReporteBXAdquirentePK resumenReporteBXAdquirentePK = new ResumenReporteBXAdquirentePK();
                    resumenReporteBXAdquirentePK.setIdAdquirente(adq);
                    //resumenReporteBXAdquirentePK.setIdReporteBXMarca(rRbXM.getIdReporteBXMarca());
                    ResumenReporteBXAdquirente resumenReporteBXAdquirente = new ResumenReporteBXAdquirente();
                    resumenReporteBXAdquirente.setResumenReporteBXAdquirentePK(resumenReporteBXAdquirentePK);
                    RegistroXAdquirente registroXAdquirente = mapaRegxAdq.get(adq);
                    resumenReporteBXAdquirente.setTotalTxnInt(registroXAdquirente.getTotalTxn());
                    resumenReporteBXAdquirente.setMontoTotalTxnInt(registroXAdquirente.getMontoTotalTxn());
                    resumenReporteBXAdquirente.setResumenReporteBXMarcas(rRbXM);
                    InstitucionesResponseData iRdata = ProveedorInstitucionesBUB.GETINSTANCE().getMapaInstitucionesAdq().get(adq);
                    resumenReporteBXAdquirente.setNombreAdquirente(iRdata!=null ? iRdata.getNombreInstitucion():"NOT FOUND");
                    lstResumenReporteBXAdquirente.add(resumenReporteBXAdquirente);
                }
                //rRbXM.setResumenReporteBXAdquirenteCollection(lstResumenReporteBXAdquirente);
                listaDeListaAdq.add(lstResumenReporteBXAdquirente);
                lstResumenReporteBXMarcas.add(rRbXM);
            }
            resumenReporteB.setResumenReporteBXMarcasCollection(lstResumenReporteBXMarcas);
            em.persist(resumenReporteB);
        }
        em.flush();
        LOG.info("Finaliza ciclo de guardado de Resumen: Total y por marcas");
        LOG.info("Inicia ciclo de guardado de Resumen: por adquirentes");
        for (Collection<ResumenReporteBXAdquirente> col : listaDeListaAdq) {
            for (ResumenReporteBXAdquirente radq : col) {
                //LOG.info("--> " + radq.getResumenReporteBXAdquirentePK() );
                radq.getResumenReporteBXAdquirentePK().setIdReporteBXMarca(radq.getResumenReporteBXMarcas().getIdReporteBXMarca());
                em.persist(radq);
                //LOG.info("--> " + radq.getResumenReporteBXMarcas().getIdReporteBXMarca());
            }
        }
        LOG.info("Finaliza ciclo de guardado de Resumen: por adquirentes");
        LOG.info("Inicia commit de del proceso");
        em.getTransaction().commit();
        LOG.info("Finaliza commit de del proceso");
    }

    @Override
    public RegistroConsultaCaratulas obtenerDatosCaratula(String fecha, String id) {
        ProveedorProperties pp = ProveedorProperties.GETINSTANCE();
        Properties props = null;
        try {
            props = pp.getPropsI();
        } catch (IOException ex) {
            LOG.info("No fue posible abrir o cerrar el archivo de propiedades.");
        }
        
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("informix_PU", props);
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin(); 
        Connection connection = em.unwrap(Connection.class);
        
        PreparedStatement statement;

        StringBuilder query = new StringBuilder("select  sum(can_05) as total_txn_caratula,sum(imp_05) ");
        query.append("as monto_total_txn_caratula,fe_ini ");
        query.append("from caratula ");
        query.append("where deb_cre ='o' and inifin ='L' and id_adq = ? ");
        query.append("and id_emi in('EG',?) and fe_ini = ? ");
        query.append("group by fe_ini");
        try {
            statement = connection.prepareStatement(query.toString());
            statement.setString(1, id);
            statement.setString(2, id);
            statement.setString(3, fecha);
            LOG.info("Inicia ejecución de consulta carátulas Emisor " + id);
            ResultSet rs = dao.executeSelectingSql(statement);
            LOG.info("Finaliza ejecución de consulta carátulas Emisor " + id);
            RegistroConsultaCaratulas rcc = null;
            while (rs.next()) {
                rcc = new RegistroConsultaCaratulas();
                rcc.setTotalTxn(rs.getString(1) != null ? new Integer(rs.getString(1)) : 0);
                rcc.setMontoTotalTxn(rs.getString(2) != null ? new BigDecimal(rs.getString(2)) : BigDecimal.ZERO);
            }
            if(rcc==null){
               rcc = new RegistroConsultaCaratulas();
               rcc.setMontoTotalTxn(BigDecimal.ZERO);
               rcc.setTotalTxn(0);
            }
            return rcc;
        } catch (SQLException | GeneraResumenReporteBException e) {
            LOG.error("Ocurrió un error al consultar las caratulas");
            System.exit(-1);
        }
        return null;
    }
    
    @Override
    public Integer obtenerIdProceso() {
        return 1;
    }
}
