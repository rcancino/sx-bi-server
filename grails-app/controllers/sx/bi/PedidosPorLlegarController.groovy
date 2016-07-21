package sx.bi

import grails.rest.*

class PedidoPorLlegarController extends RestfulController{

	static responseFormats = ['json']

	PedidoPorLlegarController(){
		super(PedidosPorLlegar)
	}

    def index(Integer max) {
		log.info 'Buscando registros de PedidosPorLlegar'+params
		if (params.calendarioId) {
			def calId = params.calendarioId
			def rows = PedidosPorLlegar.where {
				calendario.id == calId 
			}.list()
			log.info " ${rows.size()} Registros de PedidosPorLlegar para el calendario $calId"
			respond rows
			return
		}
        params.max = Math.min(max ?: 10, 100)
        
        respond listAllResources(params), model: [("${resourceName}Count".toString()): countResources()]
	}
}
