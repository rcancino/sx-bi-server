package sx.bi.kpi

import grails.transaction.Transactional
import grails.transaction.NotTransactional
import groovy.sql.Sql
import sx.bi.CalendarioBi

//@Transactional
class PapelKpiToneladasService {
    

	def dataSource_bi

	@NotTransactional
    def generar(Integer ejercicio) {

    	def cals = CalendarioBi.findAll(sort:'semana') {
    		year == ejercicio
    	}
        cals.each{
    		def kpi = PapelKpi.findByCalendario(it)
    		if(!kpi) {
    		 	kpi = new PapelKpi(calendario:it).save()
    		}
    		registrarVentas(kpi)
    		registrarMetas(kpi)
            kpi.save flush:true
    	}
        registrarKpis(ejercicio)
    }

    def registrarVentas(def kpi) {
    	def sql = new Sql(dataSource_bi)
    	sql.eachRow("""
    		SELECT tipo,
                sum(toneladas) as toneladas,
                sum(venta) as venta,
                sum(costo) as costo,
                sum(facturas_cre) as facturasCre,
                sum(facturas_con) as facturasCon,
                sum(canceladas) as canceladas,
                sum(devoluciones) as devoluciones
    		FROM fact_ventas_sem_bi 
    		where calendario_id=? 
    		group by tipo
    		""",[kpi.calendario.id]) { row ->
    		switch(row.tipo) {
    			case 'SEM':
                    kpi.ventaSemanal = row.venta
                    kpi.costoSemanal = row.costo
    				kpi.ventaSemanalTon = row.toneladas
                    kpi.facturasSemanalCre = row.facturasCre
                    kpi.facturasSemanalCon = row.facturasCon
                    kpi.canceladasSemanal = row.canceladas
                    kpi.devolucionesSemanal = row.devoluciones
    				break;
    			case 'MES':
                    kpi.ventaMensual = row.venta
                    kpi.costoMensual = row.costo
    				kpi.ventaMensualTon = row.toneladas
                    kpi.facturasMensualCre = row.facturasCre
                    kpi.facturasMensualCon = row.facturasCon
                    kpi.canceladasMensual = row.canceladas
                    kpi.devolucionesMensual = row.devoluciones
    				break;
    			case 'YEAR':
                    kpi.ventaAnual = row.venta
                    kpi.costoAnual = row.costo
    				kpi.ventaAnualTon = row.toneladas
                    kpi.facturasAnualCre = row.facturasCre
                    kpi.facturasAnualCon = row.facturasCon
                    kpi.canceladasAnual = row.canceladas
                    kpi.devolucionesAnual = row.devoluciones
    				break
    		}

    	}
    }

    

    def registrarMetas(def kpi) {
        
        def meta = 3750.00
        def cal = kpi.calendario
        def diaMes = cal.diaMes
        def metaPorDia = diaMes?meta/diaMes:0.0
        
        kpi.costoMeta = 19
        kpi.ventaSemanalTonMeta = metaPorDia * cal.diasLab
        kpi.ventaMensualTonMeta = (cal.diaAcumuladoMes*metaPorDia)
        kpi.ventaAnualTonMeta = meta * (cal.mes-1) + (cal.diaAcumuladoMes*metaPorDia)
    }

    def registrarKpis(ejercicio){
        def cals = CalendarioBi.where {year==ejercicio}.list()
        def ytd= 0
        cals.each{ cal ->
            ytd += cal.diasLab
            def acu = cal.diasLab+cal.diaAcumuladoMes+ytd
            def propSemana = cal.diasLab?(cal.diasLab*100)/acu:0
            def propMes = cal.diaAcumuladoMes?(cal.diaAcumuladoMes*100)/acu:0
            def propYear = ytd?(ytd*100)/acu:0
            def kpi = 2.5
            def kpiSem = kpi*propSemana/100
            def kpiMes = kpi* propMes/100
            def kpiYtd = kpi* propYear/100
            def papelKpi = PapelKpi.find{calendario==cal}
            if(papelKpi){
                papelKpi.with{
                    kpi = kpi
                    kpiSemanal = kpiSem
                    kpiMensual = kpiMes
                    kpiAnual = kpiYtd
                }
                papelKpi.save flush:true
            }

           //if(cal.semana == 15)
             //println "Semana:$cal.semana $cal.diasLab $cal.diaAcumuladoMes $acu $propSemana $propMes $propYear $kpiSem $kpiMes $kpiYtd"
        
        }

    }
}

