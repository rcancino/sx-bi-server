package sx.bi.kpi

import grails.rest.*
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import sx.bi.CalendarioBi

@EqualsAndHashCode(includes="id,calendario")
@ToString(includeNames=true,includePackage=false)
class PapelKpiInventario {

	String id

	CalendarioBi calendario
	
	Integer existencias = 0
	Integer minimo = 0
	Integer maximo = 0
	Integer meta = 0

	Integer alcanceNacioanalA = 0
	Integer alcanceImportadoA = 0

	Integer alcanceNacioanalB = 0
	Integer alcanceImportadoB = 0

	Integer productosDeLinea = 0

	BigDecimal desviacion = 0.0
	
	BigDecimal kpi = 0.0

    static constraints = {
    	
    }

    static mapping = {
    	id generator:'uuid'
    	
    }
}
