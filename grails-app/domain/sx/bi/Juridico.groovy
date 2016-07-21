package sx.bi

class Juridico {

	String id

	String cliente

	String nombre

	Date traspaso

	BigDecimal saldo=0

	BigDecimal pago=0

	Date ultimoPago

	String abogado

	BigDecimal mes1=0

	BigDecimal mes2=0

	BigDecimal mes3=0

	CalendarioBi calendario





    static constraints = {
    	abogado nullable:true
    	ultimoPago nullable:true
    	

    }

    static mapping = {
    	id generator:'uuid'

    }
}
