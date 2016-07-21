package sx.bi.kpi

import grails.rest.*
import static org.springframework.http.HttpStatus.*

class CalificacionController extends RestfulController{

	static responseFormats = ['json']

    CalificacionController(){
		super(Calificacion)
	}

	def index(Integer max) {
		/*
		log.info 'Buscando Calificaciones'+params
		println 'Origin: '+request.getHeader("Origin")
		def origin = request.getHeader('Origin')
		*/
		if (params.semana && params.ejercicio) {
			def semana = params.int('semana')
			def ejercicio = params.int('ejercicio')
			def calificaciones = Calificacion.where{
				calendario.semana == semana  && calendario.year == ejercicio
				}.list()
			respond calificaciones
			return
		} 
        //params.max = Math.min(max ?: 10, 100)
        params.max = 0
        respond listAllResources(params), model: [("${resourceName}Count".toString()): countResources()]
	}

	def findPartidas(){
		log.info 'Buscando partidas del calificacion: '+params
		if(params.calificacionId){
			def calId = params.calificacionId
			def partidas = CalificacionDet.where{calificacion.id == calId}.list();
			respond partidas
			return
		}
		render(status: 404, text: 'No existen partidas para la calificacion: $cal')
	}


}
