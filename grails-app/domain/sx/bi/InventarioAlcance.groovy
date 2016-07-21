package sx.bi

class InventarioAlcance {

	String id

	CalendarioBi calendario

	String linea

	BigDecimal margenPorcentajeUt=0

	BigDecimal mayorToneladas=0

	BigDecimal mayorParticipacion=0

	BigDecimal mayorProductos=0

	BigDecimal mayorCosto=0

	BigDecimal menorToneladas=0

	BigDecimal menorParticipacion=0

	BigDecimal menorProductos=0

	BigDecimal menorCosto=0

	BigDecimal menorDias1Nal=0

	BigDecimal menorDias2Nal=0

	BigDecimal menorDias1Imp=0

	BigDecimal menorDias2Imp=0

	Boolean deLinea=true



    static constraints = {
    }

    static mapping = {
    	id generator:'uuid'
    }
}
