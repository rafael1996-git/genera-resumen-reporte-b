/*
 * Este código fuente es Confidencial y también puede contener información privilegiada, es para uso exclusivo de E-Global.
 * Tenga en cuenta que cualquier distribución, copia o uso de esta información sin autorización está estrictamente prohibida.
 * Si ha identificado algún mal uso de este código fuente por favor notifiquelo a la dirección de correo seginfo@eglobal.com.mx
 */
package com.eglobal.genera.resumen.reporte.b.providers;

import com.eglobal.genera.resumen.reporte.b.beans.dao.impl.DaoATMS;
import com.eglobal.genera.resumen.reporte.b.beans.dao.impl.DaoPOS;
import com.eglobal.genera.resumen.reporte.b.constructor.ConstructorResumenATMS;
import com.eglobal.genera.resumen.reporte.b.constructor.ConstructorResumenAbstracto;
import com.eglobal.genera.resumen.reporte.b.constructor.ConstructorResumenPOS;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author egldt1134
 */
public class ProveedorProcesosDisponibles {
    public Map<Integer,ConstructorResumenAbstracto> provide(){
        Map<Integer,ConstructorResumenAbstracto> map = new HashMap<>();
        ConstructorResumenAbstracto procesoPOS = new ConstructorResumenPOS(new DaoPOS());
        map.put(1, procesoPOS);
        ConstructorResumenAbstracto procesoATMS = new ConstructorResumenATMS(new DaoATMS());
        map.put(2, procesoATMS);
        return map;
    }
}
