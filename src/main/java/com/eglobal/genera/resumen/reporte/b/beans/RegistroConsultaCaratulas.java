/*
 * Este código fuente es Confidencial y también puede contener información privilegiada, es para uso exclusivo de E-Global.
 * Tenga en cuenta que cualquier distribución, copia o uso de esta información sin autorización está estrictamente prohibida.
 * Si ha identificado algún mal uso de este código fuente por favor notifiquelo a la dirección de correo seginfo@eglobal.com.mx
 */
package com.eglobal.genera.resumen.reporte.b.beans;

import java.math.BigDecimal;

/**
 *
 * @author egldt1134
 */
public class RegistroConsultaCaratulas {
    public Integer totalTxn;
    public BigDecimal montoTotalTxn;

    public Integer getTotalTxn() {
        return totalTxn;
    }

    public void setTotalTxn(Integer totalTxn) {
        this.totalTxn = totalTxn;
    }

    public BigDecimal getMontoTotalTxn() {
        return montoTotalTxn;
    }

    public void setMontoTotalTxn(BigDecimal montoTotalTxn) {
        this.montoTotalTxn = montoTotalTxn;
    }
    
    
}
