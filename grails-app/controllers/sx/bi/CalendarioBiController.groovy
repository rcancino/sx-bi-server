package sx.bi

import grails.rest.*

class CalendarioBiController extends RestfulController{

	static responseFormats = ['json']

	CalendarioBiController(){
		super(CalendarioBi)
	}

	def index(Integer max) {
		log.info 'Buscando calendarios'+params
        params.max = Math.min(max ?: 50, 100)
        params.sort = 'semana'
        respond CalendarioBi.where {year == 2016}.list(params);
        //respond listAllResources(params), model: [("${resourceName}Count".toString()): countResources()]
	}

	

	
}
