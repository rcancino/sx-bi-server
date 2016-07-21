package sx.bi

class PedidosPorLlegar {

	String id

	CalendarioBi calendario

	String mes

	Integer pedidos

	BigDecimal toneladas

    static constraints = {
    	id generator:'uuid'
    }
}
