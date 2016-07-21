package sx.bi

import grails.rest.*

class InventarioSucursalController extends RestfulController{

	static responseFormats = ['json']

	InventarioSucursalController(){
		super(InventarioSucursal)
	}

    def index(Integer max) {
		
		if (params.calendarioId) {
			def calId = params.calendarioId
			def rows = InventarioSucursal.where {
				calendario.id == calId 
			}.list()
			log.info " ${rows.size()} Registros de InventarioSucursal para el calendario $calId"
			respond rows
			return
		}
        params.max = Math.min(max ?: 10, 100)
        log.info 'Buscando registros de InventarioSucursal'+params
        respond listAllResources(params), model: [("${resourceName}Count".toString()): countResources()]
	}

	def comprobacionDeInventario(){
		def comprobacion = [:]
		def semana = params.int('semana')
		def ejercicio = params.int('ejercicio')
		def ejercicioAnterior = ejercicio
		def semanaInicial = semana - 1
		
		//Si es semana  1 buscameos la ultima semana del ejercicio anterior
		if(semana == 1) {
			semanaInicial = 52
			ejercicioAnterior = ejercicio - 1
		}
		
		
		def inicial = InventarioSucursal.executeQuery(
		        'select sum(m.toneladas) from InventarioSucursal m where m.calendario.semana = ? and m.calendario.year=? '
		        ,[semanaInicial,ejercicioAnterior])
				.get(0)?:0.0

		def ventas = FactVentasSemBi.executeQuery(
		        'select sum(m.toneladas) from FactVentasSemBi m where m.calendario.semana = ? and m.calendario.year=? and m.tipo = ?'
		        ,[semana,ejercicio,'SEM'])
				.get(0)?:0.0

		def compras = EntradaComprasSem.executeQuery(
		        'select sum(m.kilosNal),sum(m.kilosImp) from EntradaComprasSem m where m.calendario.semana = ? and m.calendario.year=? '
		        ,[semanaInicial,ejercicio])
				.get(0)

		comprobacion.inicial = inicial
		comprobacion.ventas = ventas * - 1.0
		comprobacion.comprasNal = compras[0]?:0.0
		comprobacion.comprasImp = compras[1]?:0.0
		respond comprobacion
	}
}
