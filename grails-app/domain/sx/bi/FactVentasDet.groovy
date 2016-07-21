package sx.bi

class FactVentasDet {


	String id

	String tipo

	String origenId

	String inventarioId

	String clienteId

	String cliente 

	Integer docto

	String origen

	String sucursalId

	String suc

	Date fecha

	String lineaId

	String linea

	String marcaId

	String marca

	String claseId

	String clase

	String productoId

	String clave

	String descripcion

	String unidad

	BigDecimal  factoru = 0.0

	Integer gramos = 0.0

	BigDecimal kxmil = 0.0

	Integer calibre = 0.0

	Integer caras = 0.0

	Boolean delinea=true

	Boolean nacional=true

	BigDecimal cantidad=0

	BigDecimal kilos=0

	BigDecimal  precioL=0

	BigDecimal impBruto=0

	BigDecimal dsctoEsp=0

	BigDecimal precio=0

	BigDecimal importe=0

	BigDecimal dscto=0

	Integer cortes=0

	BigDecimal precioCortes=0

	BigDecimal impNeto=0

	BigDecimal costop=0

	BigDecimal costo=0

	BigDecimal desctoCosto=0

	BigDecimal rebate=0

	BigDecimal costoNeto=0

	Integer diaId=0

	Integer semana=0

	Long sw2


    static constraints = {
    	sw2 nullable:true
    	semana nullable:true
    	diaId nullable:true
    	dsctoEsp nullable:true
    }

    static mapping = {
    	id generator:'uuid'
    	fecha type:'date', index: 'FACVENTASDET_IDX1'
    }
}

