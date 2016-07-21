package sx.bi.kpi

import grails.transaction.Transactional
import grails.transaction.NotTransactional
import groovy.sql.Sql
import sx.bi.CalendarioBi

@Transactional
class PapelKpiInventarioService {

	def dataSource_bi

	@NotTransactional
    def generar(Integer ejercicio) {

    	def cals = CalendarioBi.findAll(sort:'semana') {
    		year == ejercicio
    	}
        cals.each{
    		def kpi = PapelKpiInventario.findByCalendario(it)
    		if(!kpi) {
    		 	kpi = new PapelKpiInventario(calendario:it).save()
    		}
    		registrarInventario(kpi)
            registrarAlcances(kpi)
    		registrarMetas(kpi)
            calcularKpi(kpi)
            kpi.save flush:true
    	}
        
    }

    def registrarInventario(def kpi){
        def sql = new Sql(dataSource_bi)
        sql.eachRow("""
            select sum(toneladas) as toneladas from inventario_sucursal where calendario_id = ?
            """,[kpi.calendario.id]) { row ->
            kpi.existencias = row.toneladas
        }
    }

    def registrarAlcances(def kpi){
        def sql = new Sql(dataSource_bi)
        sql.eachRow("""
            select calendario_id,
                sum(menor_dias1nal) as alcanceNacioanalA,
                sum(menor_dias1imp) as alcanceImportadoA,
                sum(menor_dias2imp) as alcanceImportadoB,
                sum(menor_dias2nal) as alcanceNacioanalB,
                sum(menor_productos) as menor_productos,
                sum(mayor_productos) as mayor_productos
            from inventario_alcance
            where de_linea = true and calendario_id = ?
            group by calendario_id
            """,[kpi.calendario.id]) { row ->
                
                kpi.alcanceNacioanalA = row.alcanceNacioanalA
                kpi.alcanceImportadoA = row.alcanceImportadoA
                kpi.alcanceImportadoB = row.alcanceImportadoB
                kpi.alcanceNacioanalB = row.alcanceNacioanalB
                kpi.productosDeLinea = row.menor_productos + row.mayor_productos
        }   
    }

    def registrarMetas(def kpi) {
        kpi.meta = 13000
        kpi.minimo = 12000
        kpi.maximo = 14083
    }

    def calcularKpi(def kpi){
        
        def toneladas = kpi.existencias ?:0
        def diferencia = toneladas - kpi.meta
        def meta = kpi.meta
        def desv = 0
        if(diferencia && toneladas){
            desv = (meta/toneladas -1) * 100
        }else{
            desv = (toneladas/meta - 1) * 100
        }
        def kpiMeta = 2.5
        def kpiRes = 0
        if(diferencia && toneladas){
            kpiRes = (meta/toneladas) * kpiMeta
        }else{
            kpiRes = (toneladas/meta) * kpiMeta
        }
        kpi.desviacion = desv
        kpi.kpi = kpiRes
    }
}
