/*
 * Este código fuente es Confidencial y también puede contener información privilegiada, es para uso exclusivo de E-Global.
 * Tenga en cuenta que cualquier distribución, copia o uso de esta información sin autorización está estrictamente prohibida.
 * Si ha identificado algún mal uso de este código fuente por favor notifiquelo a la dirección de correo seginfo@eglobal.com.mx
 */
package com.eglobal.genera.resumen.reporte.b.beans;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author egldt1134
 */
public class RegistroConsultaInformix {
    
    private Date fechaProceso;
    private Integer idEmisor;
    private Integer idAdquirente;
    private Integer marca;
    private Integer totalTransacciones;
    private BigDecimal montoTransacciones;
    private boolean subafiliados;
    private String bin;
    private String idCarat;

    public Date getFechaProceso() {
        return fechaProceso;
    }

    public void setFechaProceso(Date fechaProceso) {
        this.fechaProceso = fechaProceso;
    }

    public Integer getIdEmisor() {
        return idEmisor;
    }

    public void setIdEmisor(Integer idEmisor) {
        this.idEmisor = idEmisor;
    }

    public Integer getIdAdquirente() {
        return idAdquirente;
    }

    public void setIdAdquirente(Integer idAdquirente) {
        this.idAdquirente = idAdquirente;
    }

    public Integer getMarca() {
        return marca;
    }

    public void setMarca(Integer marca) {
        this.marca = marca;
    }

    public Integer getTotalTransacciones() {
        return totalTransacciones;
    }

    public void setTotalTransacciones(Integer totalTransacciones) {
        this.totalTransacciones = totalTransacciones;
    }

    public BigDecimal getMontoTransacciones() {
        return montoTransacciones;
    }

    public void setMontoTransacciones(BigDecimal montoTransacciones) {
        this.montoTransacciones = montoTransacciones;
    }

    public boolean isSubafiliados() {
        return subafiliados;
    }

    public void setSubafiliados(boolean subafiliados) {
        this.subafiliados = subafiliados;
    }

    public String getBin() {
        return bin;
    }

    public void setBin(String bin) {
        this.bin = bin;
    }

    public String getIdCarat() {
        return idCarat;
    }

    public void setIdCarat(String idCarat) {
        this.idCarat = idCarat;
    }
    
    
}
