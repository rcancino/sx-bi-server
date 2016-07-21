package sx.bi.kpi

import grails.rest.*
import static org.springframework.http.HttpStatus.*

class PapelKpiInventarioController extends RestfulController{

	static responseFormats = ['json']

    PapelKpiInventarioController(){
		super(PapelKpiInventario)
	}

	def index(Integer max) {
		log.info 'Buscando KPIs'+params
		
		if (params.calendarioId) {
			def calId = params.calendarioId
			def kpi = PapelKpiInventario.where{ calendario.id == calId }.find()
			if(kpi == null){
				//render status: NOT_FOUND
				render(status: 404, text: 'No existe KPI Inventario para el $calId')
				return
			}
			else{
				respond kpi
				return
			}
		}
        params.max = Math.min(max ?: 10, 100)
        respond listAllResources(params), model: [("${resourceName}Count".toString()): countResources()]
	}

    
}
