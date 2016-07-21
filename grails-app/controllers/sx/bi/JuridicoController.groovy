package sx.bi

import grails.rest.*

class JuridicoController extends RestfulController{

	static responseFormats = ['json']

	JuridicoController(){
		super(Juridico)
	}

    def index(Integer max) {
		log.info 'Buscando registros de Juridico'+params
        params.max = 100
        respond listAllResources(params), model: [("${resourceName}Count".toString()): countResources()]
	}
}
