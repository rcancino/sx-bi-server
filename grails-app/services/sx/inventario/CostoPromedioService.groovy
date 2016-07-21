package sx.inventario


import grails.transaction.Transactional
import groovy.sql.Sql
import org.apache.commons.lang.exception.ExceptionUtils
import sx.core.*
import sx.bi.*
import sx.Periodo

@Transactional
class CostoPromedioService {

    def importadorService

    def importadorCostoPromedio(Integer year, Integer mes){
        Sql sql=importadorService.buildSql()
        sql.eachRow("select * from sx_costos_p where year=? and mes=?",[year,mes]){ c ->
        def costoBi=CostoPromedioBi.findByClaveAndMesAndYear(c.clave,c.mes,c.year)
 			//	Producto producto=Producto.findBySw2(c.producto_id)

/*
  				CostoPromedio costo=new CostoPromedio(
								//	producto:producto,
									clave:c.clave,
									year:c.year,
									mes:c.mes,
									costop:c.costop,
									costou:c.costou,
									costor:c.costor
									)
  				importadorService.persist(costo)


                */          if(costoBi){

                  costoBi.costop=c.costop
                  costoBi.costou=c.costou
                  costoBi.costor=c.costor
              }
              else{

                  costoBi=new CostoPromedioBi(

                  //  producto:producto,
                  clave:c.clave,
                  year:c.year,
                  mes:c.mes,
                  costop:c.costop,
                  costou:c.costou,
                  costor:c.costor
                  )
              }
              costoBi.save failOnError:true,flush:true	
          }
    }



    def actualizarCosto(CalendarioBi calendario) {
        Periodo p=Periodo.getPeriodoEnUnMes(calendario.mes)
        def c = FactVentasDet.createCriteria()
        
        def result = c.scroll {
            between("fecha", p.fechaInicial, p.fechaFinal)
            //maxResults(10)
            order("clave", "asc")
        }

        while (result.next()) {
            def v = result.get()[0]
            //println "Procesando $v.clave"
            def ejercicio=calendario.year
            def cmes=calendario.mes
            def costo =CostoPromedioBi.where{
                year ==  ejercicio && mes == cmes   && clave == v.clave
            } find()

           if( (costo == null) || !costo?.costop ){
                if(cmes==1){
                    ejercicio = ejercicio-1
                    cmes = 12
                } else {
                    cmes = cmes -1
                }
                //println "Bucando costo en mes anterior ${ejercicio} - ${cmes}"
                  
                costo =CostoPromedioBi.where{
                    year ==  ejercicio && mes == cmes   && clave == v.clave
                } find()
            }
              
            if(costo){
                //println "Aplicando costo $costo.costop"
                v.costop = costo.costop
                v.costo = v.cantidad * v.costop
                v.costoNeto =  ( ((100-v.desctoCosto) / 100 ) * v.costop) * ( (100 - v.rebate)/100)
                v.save()
            }
        }

    }
}
