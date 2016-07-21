package sx.bi.kpi

import grails.rest.*
import static org.springframework.http.HttpStatus.*

class CalificacionDetController extends RestfulController{

	static responseFormats = ['json']

    CalificacionDetController(){
		super(CalificacionDet)
	}

	protected List<CalificacionDet> listAllResources(Map params) {
		if(params.calificacionId){
			def calId = params.caliricacionId
			respond CalificacionDet.where{calificacion.id == calId}.list(params)
			return
		}
        resource.list(params)
    }

}
