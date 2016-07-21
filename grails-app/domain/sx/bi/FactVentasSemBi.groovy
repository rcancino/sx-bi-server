package sx.bi

import groovy.transform.ToString
import groovy.transform.EqualsAndHashCode

@ToString(includes='id',includeNames=true,includePackage=false)
@EqualsAndHashCode(includes='id')
class FactVentasSemBi {

	String id

	CalendarioBi calendario

	String tipo

	String sucursal

	BigDecimal toneladas=0

	BigDecimal toneladasCre=0

	BigDecimal toneladasCon=0

	BigDecimal venta=0

	BigDecimal ventaCre=0

	BigDecimal ventaCon=0

	BigDecimal facturas=0

	BigDecimal facturasCre=0

	BigDecimal facturasCon=0

	Integer dias=0

	BigDecimal costo=0

	BigDecimal costoCre=0

	BigDecimal costoCon=0

	BigDecimal devoluciones=0

	BigDecimal devolucionesCre=0

	BigDecimal devolucionesCon=0

	BigDecimal utilidad=0

	BigDecimal utilidadPorcentaje=0

	BigDecimal utilidadCre=0

	BigDecimal utilidadPorcentajeCre=0

	BigDecimal utilidadCon=0

	BigDecimal utilidadPorcentajeCon=0
	
	BigDecimal canceladas=0

	BigDecimal canceladasCre=0

	BigDecimal canceladasCon=0

    static constraints = {
    	//calendario(unique: [ 'tipo','sucursal'])

    }

    static mapping = {
    	id generator:'uuid'
    }
}
