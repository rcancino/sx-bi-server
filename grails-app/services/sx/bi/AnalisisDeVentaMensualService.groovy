package sx.bi

import grails.transaction.NotTransactional
import grails.transaction.Transactional
import groovy.sql.Sql


class AnalisisDeVentaMensualService {

    def dataSource

    @Transactional
    def generar() {
        log.info 'Eliminando los registros existentes de analisis de vantas'
        AnalisisDeVentaMensual.executeUpdate('delete from AnalisisDeVentaMensual')
        def sql = new Sql(dataSource)

        def ventaNetaYtd = 0
        def costoNetoYtd = 0

        sql.eachRow(SQL_IMPORTACION) { row ->
            if (row.mes % 12 == 0) {
                ventaNetaYtd = 0
                costoNetoYtd = 0
            }
            ventaNetaYtd += row.ventaNeta
            costoNetoYtd += row.costoNeto

            def analisis = new AnalisisDeVentaMensual(
                    ejercicio: row.ejercicio,
                    mes: Mes.fromValue(row.mes),
                    ventaNeta: row.ventaNeta,
                    ventaNetaYtd: ventaNetaYtd,
                    costoNeto: row.costoNeto,
                    costoNetoYtd: costoNetoYtd


            )
            println 'Salvando :' + analisis
            analisis.save failOnError: true, flush: true

        }
    }


    String SQL_IMPORTACION = """
        select
          year(fecha) as ejercicio,
          month(fecha) as mes,
          sum(v.IMP_NETO) as ventaNeta,
          sum(v.costo_neto) as costoNeto
        FROM fact_ventas_det v
        where fecha BETWEEN '2014/01/01' and '2016/12/31'
        group by year(fecha),month(fecha)
        order by year(fecha),month(fecha)
    """
}
