package sx.bi.kpi

import grails.rest.*
import static org.springframework.http.HttpStatus.*
import sx.bi.ClienteAtrasoMax

class ClienteAtrasoMaxController extends RestfulController{

	static responseFormats = ['json']

    ClienteAtrasoMaxController(){
		super(ClienteAtrasoMax)
	}

	def index(Integer max) {
		
		
		if (params.calendarioId && params.clave) {
			def calId = params.calendarioId
			def clave = params.clave
			def cliente = ClienteAtrasoMax.where{ calendario.id == calId && clave==clave }.find()
			if(cliente == null){
				render(status: 404, text: 'No existe cliente Inventario para el $calId')
				return
			}
			else{
				respond cliente
				return
			}
		}
		else if (params.calendarioId ) {
			log.info 'Buscando clientes atraso maximo calendarioId: '+params.calendarioId
			def calId = params.calendarioId
			def clientes = ClienteAtrasoMax.where{ calendario.id == calId && clave!= 'Total'}.list()
			log.info 'Clientes atraso maximo : '+clientes.size()
			respond clientes
			return
		}
		log.info 'Buscando KPIs'+params
        params.max = Math.min(max ?: 10, 100)
        respond listAllResources(params), model: [("${resourceName}Count".toString()): countResources()]
	}

    
}
