package com.eglobal.genera.resumen.reporte.b.entities;

import com.eglobal.genera.resumen.reporte.b.entities.CEstatusArchivos;
import com.eglobal.genera.resumen.reporte.b.entities.CProcesos;
import com.eglobal.genera.resumen.reporte.b.entities.EmisoresAdeudo;
import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.7.7.v20200504-rNA", date="2023-03-14T12:02:58")
@StaticMetamodel(ArchivosReporteB.class)
public class ArchivosReporteB_ { 

    public static volatile SingularAttribute<ArchivosReporteB, Date> fechaInicioProceso;
    public static volatile SingularAttribute<ArchivosReporteB, String> nombreArchivo;
    public static volatile SingularAttribute<ArchivosReporteB, Date> fechaFinalRango;
    public static volatile SingularAttribute<ArchivosReporteB, Date> fechaInicialRango;
    public static volatile SingularAttribute<ArchivosReporteB, Date> fechaFinProceso;
    public static volatile CollectionAttribute<ArchivosReporteB, EmisoresAdeudo> emisoresAdeudoCollection;
    public static volatile SingularAttribute<ArchivosReporteB, BigDecimal> montoTotalTxn;
    public static volatile SingularAttribute<ArchivosReporteB, Date> fechaGeneracion;
    public static volatile SingularAttribute<ArchivosReporteB, Integer> idArchivo;
    public static volatile SingularAttribute<ArchivosReporteB, Integer> totalTxnInt;
    public static volatile SingularAttribute<ArchivosReporteB, CProcesos> idProceso;
    public static volatile SingularAttribute<ArchivosReporteB, CEstatusArchivos> idEstatus;

}