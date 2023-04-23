package com.eglobal.genera.resumen.reporte.b.entities;

import com.eglobal.genera.resumen.reporte.b.entities.ResumenReporteBXAdquirentePK;
import com.eglobal.genera.resumen.reporte.b.entities.ResumenReporteBXMarcas;
import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.7.7.v20200504-rNA", date="2023-03-14T12:02:58")
@StaticMetamodel(ResumenReporteBXAdquirente.class)
public class ResumenReporteBXAdquirente_ { 

    public static volatile SingularAttribute<ResumenReporteBXAdquirente, ResumenReporteBXMarcas> resumenReporteBXMarcas;
    public static volatile SingularAttribute<ResumenReporteBXAdquirente, ResumenReporteBXAdquirentePK> resumenReporteBXAdquirentePK;
    public static volatile SingularAttribute<ResumenReporteBXAdquirente, Integer> totalTxnInt;
    public static volatile SingularAttribute<ResumenReporteBXAdquirente, String> nombreAdquirente;
    public static volatile SingularAttribute<ResumenReporteBXAdquirente, BigDecimal> montoTotalTxnInt;

}