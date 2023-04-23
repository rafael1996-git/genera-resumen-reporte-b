package com.eglobal.genera.resumen.reporte.b.entities;

import com.eglobal.genera.resumen.reporte.b.entities.ArchivosReporteB;
import com.eglobal.genera.resumen.reporte.b.entities.ResumenReporteB;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.7.7.v20200504-rNA", date="2023-03-14T12:02:58")
@StaticMetamodel(CProcesos.class)
public class CProcesos_ { 

    public static volatile CollectionAttribute<CProcesos, ArchivosReporteB> archivosReporteBCollection;
    public static volatile SingularAttribute<CProcesos, String> nombreProceso;
    public static volatile SingularAttribute<CProcesos, Integer> idProceso;
    public static volatile CollectionAttribute<CProcesos, ResumenReporteB> resumenReporteBCollection;

}