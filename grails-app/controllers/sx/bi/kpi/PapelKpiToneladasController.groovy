package sx.bi.kpi

import grails.rest.*
import static org.springframework.http.HttpStatus.*

class PapelKpiToneladasController extends RestfulController{

	static responseFormats = ['json']

    PapelKpiToneladasController(){
		super(PapelKpiToneladas)
	}

	def index(Integer max) {
		log.info 'Buscando KPIs'+params
		
		if (params.calendarioId) {
			def calId = params.calendarioId
			def kpi = PapelKpiToneladas.where{ calendario.id == calId }.find()
			if(kpi == null){
				//render status: NOT_FOUND
				render(status: 404, text: 'No existe KPI Toneladas para el $calId')
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
