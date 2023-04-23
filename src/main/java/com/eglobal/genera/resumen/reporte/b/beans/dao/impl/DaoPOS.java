/*
 * Este código fuente es Confidencial y también puede contener información privilegiada, es para uso exclusivo de E-Global.
 * Tenga en cuenta que cualquier distribución, copia o uso de esta información sin autorización está estrictamente prohibida.
 * Si ha identificado algún mal uso de este código fuente por favor notifiquelo a la dirección de correo seginfo@eglobal.com.mx
 */
package com.eglobal.genera.resumen.reporte.b.beans.dao.impl;

import com.eglobal.genera.resumen.reporte.b.beans.dao.Dao;
import com.eglobal.reporte.b.commons.beans.exceptions.GeneraResumenReporteBException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import javax.persistence.Query;

/**
 *
 * @author egldt1134
 */
public class DaoPOS implements Dao {

    @Override
    public Collection obtenerDatos(Query query) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ResultSet executeSelectingSql(String query, Connection connection) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ResultSet executeSelectingSql(PreparedStatement statement) throws GeneraResumenReporteBException {

        try {
            return statement.executeQuery();
        } catch (SQLException ex) {
            throw new GeneraResumenReporteBException("Ocurrió un error al consultar a Base de Datos");
        }

    }

}
