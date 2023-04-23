package com.eglobal.genera.resumen.reporte.b.entities;

import com.eglobal.genera.resumen.reporte.b.entities.CProcesos;
import com.eglobal.genera.resumen.reporte.b.entities.ResumenReporteBXMarcas;
import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.7.7.v20200504-rNA", date="2023-03-14T12:02:58")
@StaticMetamodel(ResumenReporteB.class)
public class ResumenReporteB_ { 

    public static volatile CollectionAttribute<ResumenReporteB, ResumenReporteBXMarcas> resumenReporteBXMarcasCollection;
    public static volatile SingularAttribute<ResumenReporteB, BigDecimal> montoIntVsCarat;
    public static volatile SingularAttribute<ResumenReporteB, Date> fechaProceso;
    public static volatile SingularAttribute<ResumenReporteB, String> descEmisor;
    public static volatile SingularAttribute<ResumenReporteB, Integer> idEmisor;
    public static volatile SingularAttribute<ResumenReporteB, BigDecimal> montoTotalTxnCaratula;
    public static volatile SingularAttribute<ResumenReporteB, Integer> idInstitucionBm;
    public static volatile SingularAttribute<ResumenReporteB, BigDecimal> montoTotalTxnInt;
    public static volatile SingularAttribute<ResumenReporteB, Boolean> subafiliado;
    public static volatile SingularAttribute<ResumenReporteB, String> descIntitucionBm;
    public static volatile SingularAttribute<ResumenReporteB, Integer> txnIntVsCarat;
    public static volatile SingularAttribute<ResumenReporteB, Integer> totalTxnInt;
    public static volatile SingularAttribute<ResumenReporteB, Integer> totalTxnCaratula;
    public static volatile SingularAttribute<ResumenReporteB, CProcesos> idProceso;
    public static volatile SingularAttribute<ResumenReporteB, Integer> idRegistro;
    public static volatile SingularAttribute<ResumenReporteB, Boolean> disponible;

}