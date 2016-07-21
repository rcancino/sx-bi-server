package sx.server.integracion

class ResultadosSemanaJob {

  	def importadorFactVentsDetService

    static triggers = {
      //simple repeatInterval: 5000l // execute job once in 5 seconds
      cron name: 'Resultados', cronExpression:"0 0 22 ? * *"

    }

    def execute() {
        try {
        	log.info 'Ejecutando job...'	
            println 'Ejecutando job'
            Date hoy=new Date()
            def calendario=CalendarioBi.where{fechaFinal==hoy}.find()
        }
        catch(Exception e) {
        	
        }
        
    }
}
