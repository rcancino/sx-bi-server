package sx.bi

class MargenAnualLinea {

	String id

	CalendarioBi calendario

	String tipo

	String linea

	BigDecimal venta=0

	BigDecimal costo=0

	BigDecimal utilidad=0


    static constraints = {
    }

    static mapping = {
    	id generator:'uuid'
    }
}
