/*
 * Este código fuente es Confidencial y también puede contener información privilegiada, es para uso exclusivo de E-Global.
 * Tenga en cuenta que cualquier distribución, copia o uso de esta información sin autorización está estrictamente prohibida.
 * Si ha identificado algún mal uso de este código fuente por favor notifiquelo a la dirección de correo seginfo@eglobal.com.mx
 */
package com.eglobal.genera.resumen.reporte.b.beans.dao;

import com.eglobal.reporte.b.commons.beans.exceptions.GeneraResumenReporteBException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import javax.persistence.Query;

/**
 *
 * @author egldt1134
 */
public interface Dao {
    public Collection obtenerDatos(Query query);
    public ResultSet executeSelectingSql(String query, Connection connection);
    public ResultSet executeSelectingSql(PreparedStatement statement) throws GeneraResumenReporteBException;
}
