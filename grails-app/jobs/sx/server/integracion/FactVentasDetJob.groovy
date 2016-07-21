package sx.server.integracion

import sx.bi.*

class FactVentasDetJob {


    def importadorFactVentsDetService
    def costoPromedioService
    def importadorDeCuentasPorCobrarService
    def calificacionService

    static triggers = {
      //simple repeatInterval: 5000l // execute job once in 5 seconds
      cron name: 'FactVentasDet', cronExpression:"0 0 20 ? * *"
    }

    def execute() {
        try {
        	log.info 'Ejecutando job...'	

           
            // Obteniendo parametros para los importadores automaticos
           Date hoy=new Date ('06/23/2016')
           def calendario=CalendarioBi.find("from CalendarioBi c where ? between c.fechaInicial and c.fechaFinal ",[hoy])



          log.info 'Importando Fact Ventas Det para: '+hoy 
           importadorFactVentsDetService.importadorFactVentasDet(hoy,hoy)

          log.info 'Actualizando costo promedio para: '+calendario.mes" - "+calendario.year
            costoPromedioService.importadorCostoPromedio(calendario.year,calendario.mes)

          log.info 'Actualizando costo en Ventas: '+calendario.mes" - "+calendario.year
               costoPromedioService.actualizarCosto(calendario)
     
          log.info 'Importando Ventas canceladas para: '+calendario.semana" - "+calendario.year
           importadorFactVentsDetService.importarVentasCanceladasSemana(calendario)

          log.info 'Importando Ventas Semanales para: '+calendario.semana" - "+calendario.year
           importadorFactVentsDetService.importadorFactVentasSemana(calendario)

          log.info 'Importando Margen Anual: '+calendario.semana" - "+calendario.year
           importadorFactVentsDetService.importadorMargenAnualLinea(calendario)

          log.info 'Importando Inventario Suc: '+calendario.semana" - "+calendario.year
           importadorFactVentsDetService.importadorInventarioSucursal(calendario)

          log.info 'Importando Entrada Compras: '+calendario.semana" - "+calendario.year
           importadorFactVentsDetService.importadorEntradaComprasSem(calendario)

          log.info 'Importando Juridico: '+calendario.semana" - "+calendario.year
           importadorFactVentsDetService.importadorJuridico(calendario)

          log.info 'Importando Cxc Atraso: '+calendario.semana" - "+calendario.year
           importadorDeCuentasPorCobrarService.importadorCxCAtraso(calendario)

          log.info 'Importando Inventario Alcance: '+calendario.semana" - "+calendario.year
           importadorFactVentsDetService.importadorInventarioAlcance(calendario)

          log.info 'Generando Indicadores: '+calendario.semana" - "+calendario.year
           calificacionService.generarIndicadores(calendario)

           log.info 'Actualizando canceladas en fact Ventas Det '+hoy
           importadorFactVentsDetService.borrarCancelaciones(hoy)
            

        }
        catch(Exception e) {
        	
        }
        
    }


}
  
