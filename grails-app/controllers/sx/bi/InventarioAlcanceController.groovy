package sx.bi

import grails.rest.*

class InventarioAlcanceController extends RestfulController{

	static responseFormats = ['json']

	InventarioAlcanceController(){
		super(InventarioAlcance)
	}

    def index(Integer max) {
		
		if (params.calendarioId) {
			def calId = params.calendarioId
			def deLinea = params.deLinea?:true
			def rows = InventarioAlcance.where {
				calendario.id == calId && deLinea == deLinea
			}
			
			log.info " ${rows.size()} Registros de InventarioAlcance para el calendario $calId"
			respond rows.list()
			return
		}
        params.max = Math.min(max ?: 10, 100)
        log.info 'Buscando registros de InventarioAlcance'+params
        respond listAllResources(params), model: [("${resourceName}Count".toString()): countResources()]
	}

	def margenSemanal(){
		//log.info 'Params: '+params
		// log.info 'Buscando margen para el calendario: '+calendario
		

		def margen = [:]
		def linea = MargenAnualLinea.executeQuery(
		        'select sum(m.utilidad)/sum(m.venta) from MargenAnualLinea m where m.calendario.semana = ? and m.calendario.year=? and m.tipo = ?'
		        ,[params.int('semana'),params.int('ejercicio'),'DELINEA'])
				.get(0)?:0.0
		def especial = MargenAnualLinea.executeQuery(
		        'select sum(m.utilidad)/sum(m.venta) from MargenAnualLinea m where m.calendario.semana = ? and m.calendario.year=? and m.tipo = ?'
		        ,[params.int('semana'),params.int('ejercicio'),'ESPECIAL'])
				.get(0)?:0.0

		margen.deLinea = linea * 100
		margen.especial = especial * 100
		respond margen
	}
}
