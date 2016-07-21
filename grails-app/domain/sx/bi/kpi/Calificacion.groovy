package sx.bi.kpi


import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import sx.bi.CalendarioBi

@ToString(includes='id',includeNames=true,includePackage=false)
@EqualsAndHashCode(includes='id')
class Calificacion {

	CalendarioBi  calendario

	String nombre

	BigDecimal valor

	static hasMany = [detalles: CalificacionDet]

    static constraints = {
    }
    static mapping = {
    	detalles cascade:"all-delete-orphan"
    }
}
