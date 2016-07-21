package sx.bi.kpi


import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import sx.bi.CalendarioBi

@ToString(includes='id',includeNames=true,includePackage=false)
@EqualsAndHashCode(includes='id')
class CalificacionDet {


	Calificacion calificacion

	String tipo

	BigDecimal semana

	BigDecimal mes

	BigDecimal year


    static constraints = {
    }
}
