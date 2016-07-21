package sx.bi

class InventarioSucursal {

	String id

	CalendarioBi calendario

	String almacen

	BigDecimal toneladas=0

	BigDecimal participacion=0

	BigDecimal costo=0



    static constraints = {
    }

    static mapping = {
    	id generator:'uuid'
    }
}
