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
public class RegistroXMarca {
    private Integer idMarca;
    private Integer numeroTxn;
    private BigDecimal montoTotalTxn;

    public Integer getIdMarca() {
        return idMarca;
    }

    public void setIdMarca(Integer idMarca) {
        this.idMarca = idMarca;
    }

    public Integer getNumeroTxn() {
        return numeroTxn;
    }

    public void setNumeroTxn(Integer numeroTxn) {
        this.numeroTxn = numeroTxn;
    }

    public BigDecimal getMontoTotalTxn() {
        return montoTotalTxn;
    }

    public void setMontoTotalTxn(BigDecimal montoTotalTxn) {
        this.montoTotalTxn = montoTotalTxn;
    }
    
    
}
