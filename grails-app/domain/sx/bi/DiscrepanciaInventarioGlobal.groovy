package sx.bi

class DiscrepanciaInventarioGlobal {

	String id

	CalendarioBi calendario

	String movimiento

	BigDecimal toneladas

    static constraints = {
    }

    static mapping = {
    	id generator:'uuid'
    }
}
