/*
 * Este código fuente es Confidencial y también puede contener información privilegiada, es para uso exclusivo de E-Global.
 * Tenga en cuenta que cualquier distribución, copia o uso de esta información sin autorización está estrictamente prohibida.
 * Si ha identificado algún mal uso de este código fuente por favor notifiquelo a la dirección de correo seginfo@eglobal.com.mx
 */
package com.eglobal.genera.resumen.reporte.b;

import com.eglobal.genera.resumen.reporte.b.constructor.ConstructorResumenAbstracto;
import com.eglobal.reporte.b.commons.providers.ProveedorBinesBUB;
import com.eglobal.reporte.b.commons.providers.ProveedorInstitucionesBUB;
import com.eglobal.genera.resumen.reporte.b.providers.ProveedorProcesosDisponibles;
import com.eglobal.reporte.b.commons.providers.PathsProjectProvider;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

/**
 *
 * @author egldt1134
 */
public class GeneraResumenReporteB {

    static {
        Configurator.initialize(null, PathsProjectProvider.getConf().getHomeConfBatch() + "log4j2GRB.properties");
    }

    private static final Logger LOG = LogManager.getLogger(GeneraResumenReporteB.class);

    public static void main(String... args) {

        if (args.length != 2) {
            LOG.info("La cantidad de parámetros debe ser 2: fecha|proceso");
            System.exit(-1);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fechaS = null;
        Integer proceso = null;
        try {
            Date fecha = sdf.parse(args[0]);
            fechaS = sdf.format(fecha);
            proceso = new Integer(args[1]);
        } catch (ParseException ex) {
            LOG.info("Formato de fecha incorrecto, el formato debe ser yyyy-MM-dd");
            System.exit(-1);
        }

        Map<Integer, ConstructorResumenAbstracto> map = new ProveedorProcesosDisponibles().provide();
        ConstructorResumenAbstracto cra = map.get(proceso);

        if (cra == null) {
            LOG.info("El proceso solicitado no está disponible");
            LOG.info("Los códigos de procesos disponibles son los siguientes: ");
            for (Integer i : map.keySet()) {
                LOG.info("Código: " + i);
            }
            System.exit(-1);
        }

        LOG.info("Inicia consulta de entidades Emisoras de la BUB");
        ProveedorInstitucionesBUB.GETINSTANCE().getMapaInstitucionesEmi();
        LOG.info("Inicia consulta de entidades Emisoras de la BU");
        LOG.info("Inicia consulta de entidades Adquirente de la BUB");
        ProveedorInstitucionesBUB.GETINSTANCE().getMapaInstitucionesAdq();
        LOG.info("Finaliza consulta de entidades Adquirente de la BUB");
        LOG.info("Inicia consulta de Bines Evertec de la BUB");
        ProveedorBinesBUB.GETINSTANCE().getMapaBinesEv();
        LOG.info("Finaliza consulta de Bines Evertec de la BUB");
        cra.construirResumenes(fechaS);
    }

}
