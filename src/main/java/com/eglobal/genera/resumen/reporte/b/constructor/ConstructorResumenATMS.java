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
import com.eglobal.reporte.b.commons.beans.insumos.impl.LeerEmisoresConsultasATMS;
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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author egldt1134
 */
public class ConstructorResumenATMS extends ConstructorResumenAbstracto {

    private static final Logger LOG = LogManager.getLogger(ConstructorResumenATMS.class);

    public ConstructorResumenATMS(Dao dao) {
        super(dao);
    }

    @Override
    public Map<Integer, List<EmisoresConsultaBean>> leerEmisores() throws GeneraResumenReporteBException {
        LeerEmisoresConsultasATMS lecp = new LeerEmisoresConsultasATMS();
        Map<Integer, List<EmisoresConsultaBean>> mapaEmisores = lecp.obtenerEmisores("consultasEmisoresATMSProperties.xml");
        return mapaEmisores;
    }

    @Override
    public void guardarRegistros(String fecha) {
        ProveedorProperties pp = ProveedorProperties.GETINSTANCE();
        Properties props = null;
        try {
            props = pp.getPropsP();
        } catch (IOException ex) {
            LOG.error("Ha ocurrido un error al abrir o cerrar archivo para propiedades.");
            System.exit(-1);
        }

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("postgreSQL_PU", props);
        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();
        String proQuery = "SELECT c FROM CProcesos c where c.idProceso = :idProceso ";
        String marcaQuery = "SELECT c FROM CMarcas c";

        Query q = em.createQuery(proQuery, CProcesos.class);
        Query qm = em.createQuery(marcaQuery, CMarcas.class);

        q.setParameter("idProceso", 2);

        CProcesos cp = (CProcesos) q.getSingleResult();

        List<CMarcas> lstMarcas = qm.getResultList();

        Map<Integer, CMarcas> mapaMarcas = new HashMap<>();

        for (CMarcas c : lstMarcas) {
            mapaMarcas.put(c.getIdMarca(), c);
        }

        Map<Integer, RegistroConsultaInformix> mTotal = this.getmTotal();
        Map<Integer, Map<Integer, RegistroXMarca>> mPorMarca = this.getmPorMarca();
        Map<Integer, Map<Integer, Map<Integer, RegistroXAdquirente>>> mPorMarcaPorAdq = this.getmPorMarcaPorAdq();

        LOG.info("Se guardarán un total de " + mTotal.keySet().size() + " Emisores");
        Collection<Collection<ResumenReporteBXAdquirente>> listaDeListaAdq = new LinkedList<>();
        LOG.info("Inicia ciclo de guardado de Resumen: Total y por marcas");
        for (Integer idEmisor : mTotal.keySet()) {
            RegistroConsultaInformix registroConsultaInformix = mTotal.get(idEmisor);
            LOG.info("Inicia consulta de carátulas del emisor " + registroConsultaInformix.getIdCarat());
            RegistroConsultaCaratulas rcc = this.obtenerDatosCaratula(fecha, registroConsultaInformix.getIdEmisor() + "");
            LOG.info("Finaliza consulta de carátulas del emisor " + registroConsultaInformix.getIdEmisor());
            ResumenReporteB resumenReporteB = new ResumenReporteB();
            resumenReporteB.setFechaProceso(registroConsultaInformix.getFechaProceso());
            resumenReporteB.setIdEmisor(registroConsultaInformix.getIdEmisor());
            Map<Integer, InstitucionesResponseData> map = ProveedorInstitucionesBUB.GETINSTANCE().getMapaInstitucionesEmi();

            resumenReporteB.setDescEmisor(map.get(registroConsultaInformix.getIdEmisor()) != null ? map.get(registroConsultaInformix.getIdEmisor()).getNombreInstitucion() : "NOT FOUND");
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
            resumenReporteB.setIdProceso(cp);
            resumenReporteB.setTotalTxnCaratula(rcc.getTotalTxn());
            resumenReporteB.setMontoTotalTxnCaratula(rcc.getMontoTotalTxn());
            resumenReporteB.setTxnIntVsCarat(resumenReporteB.getTotalTxnCaratula() - resumenReporteB.getTotalTxnInt());
            resumenReporteB.setMontoIntVsCarat(resumenReporteB.getMontoTotalTxnCaratula().subtract(resumenReporteB.getMontoTotalTxnInt()));
            Map<Integer, RegistroXMarca> mapaRegxMarca = mPorMarca.get(idEmisor);
            Collection<ResumenReporteBXMarcas> lstResumenReporteBXMarcas = new ArrayList<>();

            for (Integer marca : mapaRegxMarca.keySet()) {
                ResumenReporteBXMarcas rRbXM = new ResumenReporteBXMarcas();
                rRbXM.setIdMarca(mapaMarcas.get(marca));
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
                    resumenReporteBXAdquirente.setNombreAdquirente(iRdata != null ? iRdata.getNombreInstitucion() : "NOT FOUND");
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
            java.util.logging.Logger.getLogger(ConstructorResumenATMS.class.getName()).log(Level.SEVERE, null, ex);
        }

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("informix_PU", props);
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Connection connection = em.unwrap(Connection.class);

        PreparedStatement statement;

        StringBuilder query = new StringBuilder("select count(*) as total_txn_caratula,sum( decode(tt[1,2],'01',a.importe,'31',a.importe_schg) )as monto_txn_caratula ");
        query.append(" from conciliacion:total_con2_schg a, outer conciliacion:conf_conciliacion b, outer conciliacion:conf_conciliacion c ");
        query.append(" where a.id_emisor = b.id_egl_concilia_emi and a.id_adquirente = c.id_egl_concilia_adq ");
        query.append(" and id_emisor not in('11','07','38','09') and id_adquirente not in('11','07') ");
        query.append(" and id_emisor=? ");
        query.append(" and fecha=? ");
        query.append(" group by fecha ");
        try {
            statement = connection.prepareStatement(query.toString());
            statement.setString(1, id);
            statement.setString(2, fecha);
            LOG.info("Inicia ejecución de consulta carátulas Emisor ATMS" + id);
            ResultSet rs = dao.executeSelectingSql(statement);
            LOG.info("Finaliza ejecución de consulta carátulas Emisor ATMS" + id);
            RegistroConsultaCaratulas rcc = null;
            while (rs.next()) {
                rcc = new RegistroConsultaCaratulas();
                rcc.setTotalTxn(rs.getString(1) != null ? new Integer(rs.getString(1)) : 0);
                rcc.setMontoTotalTxn(rs.getString(2) != null ? new BigDecimal(rs.getString(2)) : BigDecimal.ZERO);
            }
            if (rcc == null) {
                rcc = new RegistroConsultaCaratulas();
                rcc.setMontoTotalTxn(BigDecimal.ZERO);
                rcc.setTotalTxn(0);
            }
            return rcc;
        } catch (SQLException | GeneraResumenReporteBException e) {
            LOG.error("Ocurrió un error al consultar las caratulas", e);
            System.exit(-1);
        }
        return null;
    }

    @Override
    public Integer obtenerIdProceso() {
        return 2;
    }

}
