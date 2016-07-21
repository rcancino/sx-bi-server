package sx.bi

import groovy.transform.ToString
import groovy.transform.EqualsAndHashCode



@ToString(includes='id',includeNames=true,includePackage=false)
@EqualsAndHashCode(includes='id')
class CostoPromedioBi {

	String id

	Integer year

	Integer mes 

	String clave 

	BigDecimal costop

	BigDecimal costou

	BigDecimal costor

    static constraints = {
    	producto  nullable:true
    }

    static mapping = {
    	id generator:'uuid'

    }
}
