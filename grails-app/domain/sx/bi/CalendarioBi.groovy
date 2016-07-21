package sx.bi

import groovy.transform.ToString
import groovy.transform.EqualsAndHashCode

@ToString(includes='id',includeNames=true,includePackage=false)
@EqualsAndHashCode(includes='id')

class CalendarioBi {

	String id

	Integer year

	Integer semestre

	Integer cuatrimestre

	Integer trimestre

	Integer bimestre

	Integer mes

	Integer semana

	Date fechaInicial

	Date fechaFinal

	Integer festivo =0

	String dias

	Integer diasLab=0

	Integer diaMes=0

	Integer diaAcumuladoMes=0

	BigDecimal calificacion=0


    static constraints = {
    	dias nullable:true
    	diasLab  nullable:true
    	festivo nullable:true
    	calificacion nullable:true

    }

    static mapping = {
    	id generator:'uuid'

    }

    static CalendarioBi current() {
        def now = new Date()
        CalendarioBi.where{fechaInicial<=now && fechaFinal>=now}.find()
    }
}
