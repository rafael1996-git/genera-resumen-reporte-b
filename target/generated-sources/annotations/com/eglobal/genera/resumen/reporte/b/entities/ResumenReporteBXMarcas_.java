package com.eglobal.genera.resumen.reporte.b.entities;

import com.eglobal.genera.resumen.reporte.b.entities.CMarcas;
import com.eglobal.genera.resumen.reporte.b.entities.ResumenReporteB;
import com.eglobal.genera.resumen.reporte.b.entities.ResumenReporteBXAdquirente;
import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.7.7.v20200504-rNA", date="2023-03-14T12:02:58")
@StaticMetamodel(ResumenReporteBXMarcas.class)
public class ResumenReporteBXMarcas_ { 

    public static volatile SingularAttribute<ResumenReporteBXMarcas, Integer> idReporteBXMarca;
    public static volatile CollectionAttribute<ResumenReporteBXMarcas, ResumenReporteBXAdquirente> resumenReporteBXAdquirenteCollection;
    public static volatile SingularAttribute<ResumenReporteBXMarcas, Integer> totalTxnInt;
    public static volatile SingularAttribute<ResumenReporteBXMarcas, BigDecimal> montoTotalTxnInt;
    public static volatile SingularAttribute<ResumenReporteBXMarcas, CMarcas> idMarca;
    public static volatile SingularAttribute<ResumenReporteBXMarcas, ResumenReporteB> idRegistro;

}