package sx.bi

class EntradaComprasSem {

	String id

	CalendarioBi calendario

	BigDecimal kilos=0

	BigDecimal kilosNal=0

	BigDecimal kilosImp=0

    static constraints = {
    }

    static mapping = {
    	id generator:'uuid'
    }
}
