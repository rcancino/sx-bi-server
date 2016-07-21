package sx.bi.kpi

import sx.bi.CalendarioBi
import groovy.sql.Sql
import sx.core.*
import sx.bi.*
import sx.Periodo


class CalificacionService {
	
	def dataSource_bi

    def generarIndicadores(CalendarioBi calendario){
        generarVentas(calendario)
        generarMargen(calendario)
        generarInventario(calendario)
        generarCreditoYCxC( calendario)
    }

	
def generarVentas(CalendarioBi calendario){

  
        def calif=Calificacion.findByCalendarioAndNombre(calendario,'TONELADAS')
                if(calif){
                    return
                }

                   

       def sql = new Sql(dataSource_bi)
        def query="""
            SELECT 
            FORMA,
            SUM(IFNULL(case when tipo='SEM' THEN VENTA END,0)) AS SEMANA,
            SUM(IFNULL(case when tipo='MES' THEN VENTA END,0)) AS MES,
            SUM(IFNULL(case when tipo='YEAR' THEN VENTA END,0)) AS YEAR
            FROM (
            SELECT tipo,'TONELADAS' as FORMA,
            sum(toneladas) as VENTA
            FROM fact_ventas_sem_bi 
             where calendario_id='@CALENDARIO'
             group by tipo
             ) AS X GROUP BY X.FORMA
            """
        
        query=query.replaceAll('@CALENDARIO',calendario.id)
        
                   calif=new Calificacion(
                         calendario: calendario,
                         nombre:'TONELADAS',
                         valor:0.0
                    )
        sql.eachRow(query) { row ->
               
                 calif.addToDetalles(
                        tipo:row.forma,
                        semana:row.semana,
                         mes:row.mes,
                         year:row.year
                 )

                 calif.save failOnError:true,flush:true
                
               }
    registrarMetasToneladasYMargen(calif)
    generarPuntuacion(calif)
    resultados(calif)
        
        
}

def resultados(Calificacion calificacion){

    def venta=CalificacionDet.findByCalificacionAndTipo(calificacion,'TONELADAS')

    def meta=CalificacionDet.findByCalificacionAndTipo(calificacion,'META')

    calificacion.addToDetalles(
                    tipo:'DIFERENCIA',
                    semana:venta.semana, //-meta.semana,
                    mes:venta.mes, //-meta.mes,
                    year:venta.year  //-meta.year
                    )

                 calificacion.save failOnError:true,flush:true

    calificacion.addToDetalles(
                    tipo:'DESVIACION',
                    semana:venta.semana/meta.semana,
                    mes:venta.mes/meta.mes,
                    year:venta.year/meta.year
                    )

                 calificacion.save failOnError:true,flush:true   


   def puntuacion=CalificacionDet.findByCalificacionAndTipo(calificacion,'PUNTUACION')
        

   calificacion.addToDetalles(
                    tipo:'CALIFICACION',
                    semana:(puntuacion.semana*(venta.semana/meta.semana))>puntuacion.semana?puntuacion.semana:(puntuacion.semana*(venta.semana/meta.semana)),
                    mes:(puntuacion.mes*(venta.mes/meta.mes))>puntuacion.mes?puntuacion.mes:(puntuacion.mes*(venta.mes/meta.mes)),
                    year:(puntuacion.year*(venta.year/meta.year))>puntuacion.year?puntuacion.year:(puntuacion.year*(venta.year/meta.year))
                    )
       
                 calificacion.save failOnError:true,flush:true 

def c=CalificacionDet.findByCalificacionAndTipo(calificacion,'CALIFICACION') 

    calificacion.valor=c.semana+c.mes+c.year

    calificacion.save failOnError:true,flush:true                 

}


def  registrarMetasToneladasYMargen(Calificacion calif){
        

        def meta = 3750.00
        def cal = calif.calendario
        def diaMes = cal.diaMes
        def metaPorDia = diaMes?meta/diaMes:0.0
        
       
        def ventaSemanalTonMeta = metaPorDia * cal.diasLab
        def ventaMensualTonMeta = (cal.diaAcumuladoMes*metaPorDia)
        def ventaAnualTonMeta = meta * (cal.mes-1) + (cal.diaAcumuladoMes*metaPorDia)

        
            calif.addToDetalles(
                        tipo:'META',
                        semana:ventaSemanalTonMeta,
                         mes:ventaMensualTonMeta,
                         year:ventaAnualTonMeta
                 )
         
                
                 calif.save failOnError:true,flush:true
        


         
                
}


def  generarMargen(CalendarioBi calendario){

     def calif=Calificacion.findByCalendarioAndNombre(calendario,'MARGEN')
                if(calif){
                    return
                }


       def sql = new Sql(dataSource_bi)
        def query="""
            SELECT 
            FORMA,
            SUM(IFNULL(case when tipo='SEM' THEN VENTA END,0)) AS SEMANA,
            SUM(IFNULL(case when tipo='MES' THEN VENTA END,0)) AS MES,
            SUM(IFNULL(case when tipo='YEAR' THEN VENTA END,0)) AS YEAR
            FROM (
            SELECT tipo,'VENTA' as FORMA,
            sum(venta) as VENTA
            FROM fact_ventas_sem_bi 
             where calendario_id='@CALENDARIO'
             group by tipo
             UNION
             SELECT tipo,'COSTO' as FORMA,
            sum(costo) as VENTA
            FROM fact_ventas_sem_bi 
             where calendario_id='@CALENDARIO'
             group by tipo
             ) AS X GROUP BY X.FORMA
            """
        
        query=query.replaceAll('@CALENDARIO',calendario.id)
        
            calif=new Calificacion(
                         calendario: calendario,
                         nombre:'MARGEN',
                         valor:0.0
                    )
        sql.eachRow(query) { row ->
               
                 calif.addToDetalles(
                        tipo:row.forma,
                        semana:row.semana,
                         mes:row.mes,
                         year:row.year
                 )

                 calif.save failOnError:true,flush:true
                
               }
    
    generarPuntuacion(calif)
    resultadosMargen(calif)

}

def resultadosMargen(Calificacion calificacion){

    def venta=CalificacionDet.findByCalificacionAndTipo(calificacion,'VENTA')

    def costo=CalificacionDet.findByCalificacionAndTipo(calificacion,'COSTO')

    calificacion.addToDetalles(
                    tipo:'UTILIDAD',
                    semana:venta.semana-costo.semana,
                    mes:venta.mes-costo.mes,
                    year:venta.year-costo.year
                    )

                 calificacion.save failOnError:true,flush:true

    calificacion.addToDetalles(
                    tipo:'PORC_UT',
                    semana:(venta.semana-costo.semana)*100/venta.semana,
                    mes:(venta.mes-costo.mes)*100/venta.mes,
                    year:(venta.year-costo.year)*100/venta.year
                    )

                 calificacion.save failOnError:true,flush:true

                 def margenMeta= 19

    calificacion.addToDetalles(
                    tipo:'META',
                    semana:margenMeta,
                    mes:margenMeta,
                    year:margenMeta
                    )

                 calificacion.save failOnError:true,flush:true


   calificacion.addToDetalles(
                    tipo:'DESVIACION',
                    semana:((venta.semana-costo.semana)*100/venta.semana)/margenMeta*100,
                    mes:((venta.mes-costo.mes)*100/venta.mes)/margenMeta*100,
                    year:((venta.year-costo.year)*100/venta.year)/margenMeta*100
                    )

                 calificacion.save failOnError:true,flush:true   

    def puntuacion=CalificacionDet.findByCalificacionAndTipo(calificacion,'PUNTUACION')
        

   calificacion.addToDetalles(
                    tipo:'CALIFICACION',
                    semana:(puntuacion.semana*(((venta.semana-costo.semana)*100/venta.semana)/margenMeta*100))>puntuacion.semana?puntuacion.semana:(puntuacion.semana*(((venta.semana-costo.semana)*100/venta.semana)/margenMeta*100)),
                    mes:(puntuacion.mes*(((venta.mes-costo.mes)*100/venta.mes)/margenMeta*100))>puntuacion.mes?puntuacion.mes:(puntuacion.mes*(((venta.mes-costo.mes)*100/venta.mes)/margenMeta*100)),
                    year:(puntuacion.year*(((venta.year-costo.year)*100/venta.year)/margenMeta*100))>puntuacion.year?puntuacion.year:(puntuacion.year*(((venta.year-costo.year)*100/venta.year)/margenMeta*100))
                    )
    
                 calificacion.save failOnError:true,flush:true

    def c=CalificacionDet.findByCalificacionAndTipo(calificacion,'CALIFICACION') 

    calificacion.valor=c.semana+c.mes+c.year

    calificacion.save failOnError:true,flush:true

}

def generarPuntuacion(Calificacion calificacion){
     def indicador = 2.5
        def indicadorSem = 0.0
        def indicadorMes = 0.0
        def indicadorYtd = 0.0

     def cals = CalendarioBi.where {year==calificacion.calendario.year}.list()
        def ytd= 0
        cals.each{ cal ->
            ytd += cal.diasLab
            def acu = cal.diasLab+cal.diaAcumuladoMes+ytd
            def propSemana = cal.diasLab?(cal.diasLab*100)/acu:0
            def propMes = cal.diaAcumuladoMes?(cal.diaAcumuladoMes*100)/acu:0
            def propYear = ytd?(ytd*100)/acu:0
            
            indicadorSem = indicador*propSemana/100
            indicadorMes = indicador*propMes/100
            indicadorYtd = indicador*propYear/100


            }

        calificacion.addToDetalles(
                    tipo:'PUNTUACION',
                    semana:indicadorSem,
                    mes:indicadorMes,
                    year:indicadorYtd
                    )
       // calificacion.valor=indicadorSem + indicadorMes +indicadorYtd 
                 calificacion.save failOnError:true,flush:true  

}

def generarInventario(CalendarioBi calendario){


     def calif=Calificacion.findByCalendarioAndNombre(calendario,'INVENTARIO')
                if(calif){
                    return
                }
        def sql = new Sql(dataSource_bi)
        def query="""
                SELECT 'TONELADAS' AS forma ,sum(toneladas) as semana,0 as mes,0 as year FROM inventario_sucursal where calendario_id='@CALENDARIO'
            """
        
        query=query.replaceAll('@CALENDARIO',calendario.id)

        def minimo= 12000
        def meta= 13000
        def maximo= 14083
        def toneladas=0.0

         calif=new Calificacion(
                         calendario: calendario,
                         nombre:'INVENTARIO',
                         valor:0.0
                    )
        sql.eachRow(query) { row ->
               
                 calif.addToDetalles(
                        tipo:row.forma,
                        semana:row.semana,
                         mes:row.mes,
                         year:row.year
                 )

                 calif.save failOnError:true,flush:true
                 toneladas=row.semana
               }
          

        calif.addToDetalles(
                    tipo:'MINIMO',
                    semana:minimo,
                    mes:0.0,
                    year:0.0
                    )

        calif.save failOnError:true,flush:true

        calif.addToDetalles(
                    tipo:'META',
                    semana:meta,
                    mes:0.0,
                    year:0.0
                    )

        calif.save failOnError:true,flush:true

        calif.addToDetalles(
                    tipo:'MAXIMO',
                    semana:maximo,
                    mes:0.0,
                    year:0.0
                    )

        calif.save failOnError:true,flush:true

        calif.addToDetalles(
                    tipo:'DESVIACION',
                    semana:toneladas>meta?meta/toneladas: toneladas/meta,
                    mes:0.0,
                    year:0.0
                    )

        calif.save failOnError:true,flush:true


        calif.addToDetalles(
                    tipo:'PUNTUACION',
                    semana:2.5,
                    mes:0.0,
                    year:0.0
                    )

        calif.save failOnError:true,flush:true

        calif.addToDetalles(
                    tipo:'CALIFICACION',
                    semana:(2.5*(toneladas>meta?meta/toneladas: toneladas/meta))>meta?meta:(2.5*(toneladas>meta?meta/toneladas: toneladas/meta)),
                    mes:0.0,
                    year:0.0
                    )
        calif.valor=(2.5*(toneladas>meta?meta/toneladas: toneladas/meta))>meta?meta:(2.5*(toneladas>meta?meta/toneladas: toneladas/meta))

        calif.save failOnError:true,flush:true

        generarAlcance(calif,sql)


}

def generarAlcance(Calificacion calificacion,Sql sql){

    
        def query="""
           SELECT 'ALCANCE<1.0' AS forma,sum(menor_dias1nal)as semana,SUM(menor_dias1imp) as MES,0.0 AS YEAR
            FROM inventario_alcance 
            where calendario_id='@CALENDARIO'
            union
            SELECT 'ALCANCE<0.5' AS forma,sum(menor_dias2nal)as semana,SUM(menor_dias2imp) as MES,0.0 AS YEAR
            FROM inventario_alcance 
            where calendario_id='@CALENDARIO'
            UNION
            SELECT 'PRODUCTOS DE LINEA' AS forma,0.0 as semana,sum(mayor_productos+ menor_productos )as  MES,0.0 AS YEAR
            FROM inventario_alcance 
            where calendario_id='@CALENDARIO' and de_linea is true
            """
        
        query=query.replaceAll('@CALENDARIO',calificacion.calendario.id)

        sql.eachRow(query) { row ->
               
                 calificacion.addToDetalles(
                        tipo:row.forma,
                        semana:row.semana,
                         mes:row.mes,
                         year:row.year
                 )

                 calificacion.save failOnError:true,flush:true
                
               }
}

def generarCreditoYCxC(CalendarioBi calendario){


     def calif=Calificacion.findByCalendarioAndNombre(calendario,'CREDITO Y CXC')
                if(calif){
                    return
                }

    def sql = new Sql(dataSource_bi)
        def query="""
            SELECT 'SALDO' AS FORMA,saldo as semana,100.00 as mes,atraso_max as year
                FROM cliente_atraso_max 
                where calendario_id='@CALENDARIO' and clave='total'
                union
                SELECT 'X VENCER' AS FORMA,por_vencer as semana,ROUND(por_vencer*100/SALDO,2) as mes,0.0 as year
                FROM cliente_atraso_max 
                where calendario_id='@CALENDARIO' and clave='total'
                union
                SELECT 'VENCIDO' AS FORMA,vencido as semana
                ,ROUND(vencido*100/SALDO,2) as mes
                ,0.0 as year
                FROM cliente_atraso_max 
                where calendario_id='@CALENDARIO' and clave='total'
                union
                SELECT '1 A 30' AS FORMA,atraso1a30 as semana
                ,ROUND(atraso1a30*100/SALDO,2) as mes
                ,0.0 as year
                FROM cliente_atraso_max 
                where calendario_id='@CALENDARIO' and clave='total'
                union
                SELECT '31 A 60' AS FORMA,atraso31a60 as semana
                ,ROUND(atraso31a60*100/SALDO,2) as mes
                ,0.0 as year
                FROM cliente_atraso_max 
                where calendario_id='@CALENDARIO' and clave='total'
                union
                SELECT '61 A 90' AS FORMA,atraso61a90 as semana
                ,ROUND(atraso61a90*100/SALDO,2) as mes
                ,0.0 as year
                FROM cliente_atraso_max 
                where calendario_id='@CALENDARIO' and clave='total'
                union
                SELECT '> 90' AS FORMA,atrasomas91 as semana
                ,ROUND(atrasomas91*100/SALDO,2) as mes
                ,0.0 as year
                FROM cliente_atraso_max 
                where calendario_id='@CALENDARIO' and clave='total'
                UNION
                SELECT '> 30 META' AS FORMA,atrasomas30 as semana
                ,ROUND(atrasomas30*100/SALDO,2) as mes
                ,0.0 as year
                FROM cliente_atraso_max 
                where calendario_id='@CALENDARIO' and clave='total'
            """
        
        query=query.replaceAll('@CALENDARIO',calendario.id)

                def aMayor30=0.0
        
             calif=new Calificacion(
                         calendario: calendario,
                         nombre:'CREDITO Y CXC',
                         valor:0.0
                    )
        sql.eachRow(query) { row ->
               
                 calif.addToDetalles(
                        tipo:row.forma,
                        semana:row.semana,
                         mes:row.mes,
                         year:row.year
                 )
                   if(row.forma=='> 30 META'){
                        aMayor30=row.mes
                   }
                 calif.save failOnError:true,flush:true
                
               }

            def meta=4.00

            calif.addToDetalles(
                        tipo:'META',
                        semana:0.0,
                         mes:meta,
                         year:0.0
                 )

        calif.save failOnError:true,flush:true

         calif.addToDetalles(
                        tipo:'DESVIACION',
                        semana:0.0,
                         mes:meta/aMayor30,
                         year:0.0
                 )

        calif.save failOnError:true,flush:true

        calif.addToDetalles(
                    tipo:'PUNTUACION',
                    semana:0.0,
                    mes:2.5,
                    year:0.0
                    )

        calif.save failOnError:true,flush:true

        calif.addToDetalles(
                    tipo:'CALIFICACION',
                    semana:0.0,
                    mes:(meta/aMayor30)>1?2.5:((meta/aMayor30)*2.5),
                    year:0.0
                    )
        calif.valor=(meta/aMayor30)>1?2.5:((meta/aMayor30)*2.5)

        calif.save failOnError:true,flush:true



}




}

