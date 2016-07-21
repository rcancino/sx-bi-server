package sx.bi

import groovy.transform.ToString
import groovy.transform.EqualsAndHashCode

@ToString(includes='razonSocial,rfc',includeNames=true,includePackage=false)
@EqualsAndHashCode(includes='razonSocial,rfc,grupoRfc')
class ClienteAtrasoMax {

	String id 

	CalendarioBi calendario

	String clave

	String nombre

	Integer plazo=0

	BigDecimal lineaCredito=0

	String  vencimiento

	Integer facturas=0

	Integer atrasoMax=0

	BigDecimal saldo=0

	BigDecimal porVencer=0

	BigDecimal vencido=0

	BigDecimal atraso1a30=0

	BigDecimal atraso31a60=0

	BigDecimal atraso61a90=0

	BigDecimal atrasomas91=0

	BigDecimal atrasomas30=0

    static constraints = {
    }


}
