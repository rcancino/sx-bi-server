package sx.bi.kpi

import grails.rest.*
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import sx.bi.CalendarioBi

@EqualsAndHashCode(includes="id,calendario")
@ToString(includeNames=true,includePackage=false)
class PapelKpiToneladas {

	Long id

	CalendarioBi calendario

	Kpi semanal
	Kpi mensual
	Kpi anual

	BigDecimal kpi = 0.0

    static constraints = {
    	kpiMensual scale:4
    	kpiSemanal scale:4
    	kpiAnual scale:4
    }

    static mapping = {

    }

    static embedded = ['mensual','semanal','anual']
}

class Kpi {

	BigDecimal importe
	BigDecimal meta
	BigDecimal kpi
	BigDecimal diferencia
	BigDecimal desviacion

	static mapping = {

	}
}
