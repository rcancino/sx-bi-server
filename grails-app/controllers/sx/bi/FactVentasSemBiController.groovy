package sx.bi

import grails.rest.*

class FactVentasSemBiController extends RestfulController{

	static responseFormats = ['json']

	FactVentasSemBiController(){
		super(FactVentasSemBi)
	}

    def index(Integer max) {
		
		
		
		if (params.calendarioId) {
			def calId = params.calendarioId
			def rows = FactVentasSemBi.where {
				calendario.id == calId 
			}.list()
			log.info " ${rows.size()} Registros de FactVentasSemBi para el calendario $calId"
			respond rows
			return
		}
        params.max = Math.min(max ?: 10, 100)
        log.info 'Buscando registros de FactVentasSemBi'+params
        respond listAllResources(params), model: [("${resourceName}Count".toString()): countResources()]
	}
}
