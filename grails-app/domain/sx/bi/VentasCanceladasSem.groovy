package sx.bi

class VentasCanceladasSem {

	String id

	CalendarioBi calendario

	String tipo

	String sucursal

	Integer cancelado=0

	Integer canceladoCre=0

	Integer canceladoCon=0



    static constraints = {

    }

    static mapping = {
    	id generator:'uuid'
    }
}
