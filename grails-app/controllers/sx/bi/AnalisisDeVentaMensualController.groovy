package sx.bi

import grails.rest.*

class AnalisisDeVentaMensualController extends RestfulController{

    static responseFormats = ['json']

    AnalisisDeVentaMensualController() {
        super(AnalisisDeVentaMensual)
    }

    def index(Integer max) {
        params.max = 100
        respond listAllResources(params), model: [("${resourceName}Count".toString()): countResources()]
    }

    @Override
    protected List listAllResources(Map params) {
        def q = AnalisisDeVentaMensual.where {}
        if(params.ejercicio){
           q = q.where {
               ejercicio == params.ejercicio
           }
        }
        return q.list(params)
    }
}
