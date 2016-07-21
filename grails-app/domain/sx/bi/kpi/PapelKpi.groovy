package sx.bi.kpi

import grails.rest.*
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import sx.bi.CalendarioBi

@EqualsAndHashCode(includes="id,calendario")
@ToString(includeNames=true,includePackage=false)
class PapelKpi {

	String id

	CalendarioBi calendario

	BigDecimal ventaSemanalTon = 0.0
	BigDecimal ventaMensualTon = 0.0
	BigDecimal ventaAnualTon = 0.0	

	BigDecimal ventaSemanalTonMeta = 0.0
	BigDecimal ventaMensualTonMeta = 0.0
	BigDecimal ventaAnualTonMeta = 0.0	

	BigDecimal ventaSemanal = 0.0
	BigDecimal ventaMensual = 0.0
	BigDecimal ventaAnual = 0.0

	BigDecimal ventaSemanalMeta = 0.0
	BigDecimal ventaMensualMeta = 0.0
	BigDecimal ventaAnualMeta = 0.0

	BigDecimal costoSemanal = 0.0
	BigDecimal costoMensual = 0.0
	BigDecimal costoAnual = 0.0

	BigDecimal costoMeta = 0.0


	BigDecimal kpi = 0.0

	BigDecimal kpiSemanal = 0.0
	BigDecimal kpiMensual = 0.0
	BigDecimal kpiAnual = 0.0

	Integer facturasSemanalCre = 0
	Integer facturasMensualCre = 0
	Integer facturasAnualCre = 0

	Integer facturasSemanalCon = 0
	Integer facturasMensualCon = 0
	Integer facturasAnualCon = 0

	Integer canceladasSemanal = 0
	Integer canceladasMensual = 0
	Integer canceladasAnual = 0

	Integer devolucionesSemanal = 0
	Integer devolucionesMensual = 0
	Integer devolucionesAnual = 0

	

    static constraints = {
    	kpiMensual scale:4
    	kpiSemanal scale:4
    	kpiAnual scale:4
    	
    }

    static mapping = {
    	id generator:'uuid'
    	
    }
}
