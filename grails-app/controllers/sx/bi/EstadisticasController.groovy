package sx.bi

import grails.rest.*
import groovy.sql.Sql

class EstadisticasController {

	def dataSource
	

	EstadisticasController(){
	}

	def comparativoCalificacion(){
		//def sql = new Sql(dataSource)
    String q = """
			SELECT a.semana
			,sum(case when a.year=2016 then a.calificacion else 0 end)  as year1
			,sum(case when a.year=2016-1 then a.calificacion else 0 end)  as year2
			FROM (
			SELECT c.year,c.semana,c.calificacion FROM calendario_bi c where c.year>=2016-1
			) as a
			group by a.semana
		"""
		def sql = new Sql(dataSource)
		def rows = sql.rows(q)
		respond rows,[formats:['json']]
	}

	def margenVenta(){
		//def sql = new Sql(dataSource)
    String q = """
			SELECT year(v.fecha) as YEAR		
			,ROUND (SUM(case when month(v.fecha)=1 then (V.IMP_NETO-V.COSTO_NETO)/1000 else 0 end),2) AS ENE
			,ROUND (SUM(case when month(v.fecha)=2 then (V.IMP_NETO-V.COSTO_NETO)/1000 else 0 end),2) AS FEB
			,ROUND (SUM(case when month(v.fecha)=3 then (V.IMP_NETO-V.COSTO_NETO)/1000 else 0 end),2) AS MAR
			,ROUND (SUM(case when month(v.fecha)=4 then (V.IMP_NETO-V.COSTO_NETO)/1000 else 0 end),2) AS ABR
			,ROUND (SUM(case when month(v.fecha)=5 then (V.IMP_NETO-V.COSTO_NETO)/1000 else 0 end),2) AS MAY
			,ROUND (SUM(case when month(v.fecha)=6 then (V.IMP_NETO-V.COSTO_NETO)/1000 else 0 end),2) AS JUN
			,ROUND (SUM(case when month(v.fecha)=7 then (V.IMP_NETO-V.COSTO_NETO)/1000 else 0 end),2) AS JUL
			,ROUND (SUM(case when month(v.fecha)=8 then (V.IMP_NETO-V.COSTO_NETO)/1000 else 0 end),2) AS AGO
			,ROUND (SUM(case when month(v.fecha)=9 then (V.IMP_NETO-V.COSTO_NETO)/1000 else 0 end),2) AS SEP
			,ROUND (SUM(case when month(v.fecha)=10 then (V.IMP_NETO-V.COSTO_NETO)/1000 else 0 end),2) AS OCT
			,ROUND (SUM(case when month(v.fecha)=11 then (V.IMP_NETO-V.COSTO_NETO)/1000 else 0 end),2) AS NOV
			,ROUND (SUM(case when month(v.fecha)=12 then (V.IMP_NETO-V.COSTO_NETO)/1000 else 0 end),2) AS DIC
			FROM fact_ventas_det v 
			where fecha BETWEEN '2014/01/01' and '2016/12/31'
			group by year(v.fecha)
		"""
		def sql = new Sql(dataSource)
		def rows = sql.rows(q)
		respond rows,[formats:['json']]
	}


	def margenPorcentaje(){
		//def sql = new Sql(dataSource)
    String q = """
    SELECT year(v.fecha) as YEAR
	,IFNULL(ROUND((SUM(case when month(v.fecha)=1 then V.IMP_NETO else 0 end)-SUM(case when month(v.fecha)=1 then V.COSTO_NETO else 0 end))*100/SUM(case when month(v.fecha)=1 then V.IMP_NETO else 0 end),2),0) AS ENE
	,IFNULL(ROUND((SUM(case when month(v.fecha)=2 then V.IMP_NETO else 0 end)-SUM(case when month(v.fecha)=2 then V.COSTO_NETO else 0 end))*100/SUM(case when month(v.fecha)=2 then V.IMP_NETO else 0 end),2),0) AS FEB
	,IFNULL(ROUND((SUM(case when month(v.fecha)=3 then V.IMP_NETO else 0 end)-SUM(case when month(v.fecha)=3 then V.COSTO_NETO else 0 end))*100/SUM(case when month(v.fecha)=3 then V.IMP_NETO else 0 end),2),0) AS MAR
	,IFNULL(ROUND((SUM(case when month(v.fecha)=4 then V.IMP_NETO else 0 end)-SUM(case when month(v.fecha)=4 then V.COSTO_NETO else 0 end))*100/SUM(case when month(v.fecha)=4 then V.IMP_NETO else 0 end),2),0) AS ABR
	,IFNULL(ROUND((SUM(case when month(v.fecha)=5 then V.IMP_NETO else 0 end)-SUM(case when month(v.fecha)=5 then V.COSTO_NETO else 0 end))*100/SUM(case when month(v.fecha)=5 then V.IMP_NETO else 0 end),2),0) AS MAY
	,IFNULL(ROUND((SUM(case when month(v.fecha)=6 then V.IMP_NETO else 0 end)-SUM(case when month(v.fecha)=6 then V.COSTO_NETO else 0 end))*100/SUM(case when month(v.fecha)=6 then V.IMP_NETO else 0 end),2),0) AS JUN
	,IFNULL(ROUND((SUM(case when month(v.fecha)=7 then V.IMP_NETO else 0 end)-SUM(case when month(v.fecha)=7 then V.COSTO_NETO else 0 end))*100/SUM(case when month(v.fecha)=7 then V.IMP_NETO else 0 end),2),0) AS JUL
	,IFNULL(ROUND((SUM(case when month(v.fecha)=8 then V.IMP_NETO else 0 end)-SUM(case when month(v.fecha)=8 then V.COSTO_NETO else 0 end))*100/SUM(case when month(v.fecha)=8 then V.IMP_NETO else 0 end),2),0) AS AGO
	,IFNULL(ROUND((SUM(case when month(v.fecha)=9 then V.IMP_NETO else 0 end)-SUM(case when month(v.fecha)=9 then V.COSTO_NETO else 0 end))*100/SUM(case when month(v.fecha)=9 then V.IMP_NETO else 0 end),2),0) AS SEP
	,IFNULL(ROUND((SUM(case when month(v.fecha)=10 then V.IMP_NETO else 0 end)-SUM(case when month(v.fecha)=10 then V.COSTO_NETO else 0 end))*100/SUM(case when month(v.fecha)=10 then V.IMP_NETO else 0 end),2),0) AS OCT
	,IFNULL(ROUND((SUM(case when month(v.fecha)=11 then V.IMP_NETO else 0 end)-SUM(case when month(v.fecha)=11 then V.COSTO_NETO else 0 end))*100/SUM(case when month(v.fecha)=11 then V.IMP_NETO else 0 end),2),0) AS NOV
	,IFNULL(ROUND((SUM(case when month(v.fecha)=12 then V.IMP_NETO else 0 end)-SUM(case when month(v.fecha)=12 then V.COSTO_NETO else 0 end))*100/SUM(case when month(v.fecha)=12 then V.IMP_NETO else 0 end),2),0) AS ENE
	FROM fact_ventas_det v 
	where fecha BETWEEN '2014/01/01' and '2016/12/31'
	group by year(v.fecha)
		"""
		def sql = new Sql(dataSource)
		def rows = sql.rows(q)
		respond rows,[formats:['json']]
	}

	def margenYtd(){
		//def sql = new Sql(dataSource)
		String q = """
    SELECT year(v.fecha) as YEAR
	,IFNULL(ROUND((SUM(case when month(v.fecha)=1 then V.IMP_NETO else 0 end)-SUM(case when month(v.fecha)=1 then V.COSTO_NETO else 0 end))*100/SUM(case when month(v.fecha)=1 then V.IMP_NETO else 0 end),2),0) AS ENE
	,IFNULL(ROUND((SUM(case when month(v.fecha)=2 then V.IMP_NETO else 0 end)-SUM(case when month(v.fecha)=2 then V.COSTO_NETO else 0 end))*100/SUM(case when month(v.fecha)=2 then V.IMP_NETO else 0 end),2),0) AS FEB
	,IFNULL(ROUND((SUM(case when month(v.fecha)=3 then V.IMP_NETO else 0 end)-SUM(case when month(v.fecha)=3 then V.COSTO_NETO else 0 end))*100/SUM(case when month(v.fecha)=3 then V.IMP_NETO else 0 end),2),0) AS MAR
	,IFNULL(ROUND((SUM(case when month(v.fecha)=4 then V.IMP_NETO else 0 end)-SUM(case when month(v.fecha)=4 then V.COSTO_NETO else 0 end))*100/SUM(case when month(v.fecha)=4 then V.IMP_NETO else 0 end),2),0) AS ABR
	,IFNULL(ROUND((SUM(case when month(v.fecha)=5 then V.IMP_NETO else 0 end)-SUM(case when month(v.fecha)=5 then V.COSTO_NETO else 0 end))*100/SUM(case when month(v.fecha)=5 then V.IMP_NETO else 0 end),2),0) AS MAY
	,IFNULL(ROUND((SUM(case when month(v.fecha)=6 then V.IMP_NETO else 0 end)-SUM(case when month(v.fecha)=6 then V.COSTO_NETO else 0 end))*100/SUM(case when month(v.fecha)=6 then V.IMP_NETO else 0 end),2),0) AS JUN
	,IFNULL(ROUND((SUM(case when month(v.fecha)=7 then V.IMP_NETO else 0 end)-SUM(case when month(v.fecha)=7 then V.COSTO_NETO else 0 end))*100/SUM(case when month(v.fecha)=7 then V.IMP_NETO else 0 end),2),0) AS JUL
	,IFNULL(ROUND((SUM(case when month(v.fecha)=8 then V.IMP_NETO else 0 end)-SUM(case when month(v.fecha)=8 then V.COSTO_NETO else 0 end))*100/SUM(case when month(v.fecha)=8 then V.IMP_NETO else 0 end),2),0) AS AGO
	,IFNULL(ROUND((SUM(case when month(v.fecha)=9 then V.IMP_NETO else 0 end)-SUM(case when month(v.fecha)=9 then V.COSTO_NETO else 0 end))*100/SUM(case when month(v.fecha)=9 then V.IMP_NETO else 0 end),2),0) AS SEP
	,IFNULL(ROUND((SUM(case when month(v.fecha)=10 then V.IMP_NETO else 0 end)-SUM(case when month(v.fecha)=10 then V.COSTO_NETO else 0 end))*100/SUM(case when month(v.fecha)=10 then V.IMP_NETO else 0 end),2),0) AS OCT
	,IFNULL(ROUND((SUM(case when month(v.fecha)=11 then V.IMP_NETO else 0 end)-SUM(case when month(v.fecha)=11 then V.COSTO_NETO else 0 end))*100/SUM(case when month(v.fecha)=11 then V.IMP_NETO else 0 end),2),0) AS NOV
	,IFNULL(ROUND((SUM(case when month(v.fecha)=12 then V.IMP_NETO else 0 end)-SUM(case when month(v.fecha)=12 then V.COSTO_NETO else 0 end))*100/SUM(case when month(v.fecha)=12 then V.IMP_NETO else 0 end),2),0) AS ENE
	FROM fact_ventas_det v
	where fecha BETWEEN '2014/01/01' and '2016/12/31'
	group by year(v.fecha)
		"""
		def sql = new Sql(dataSource)
		def rows = sql.rows(q)
		respond rows,[formats:['json']]
	}

	def toneladas(){
		//def sql = new Sql(dataSource)
    String q = """
	SELECT year(v.fecha) as "ao"
	,SUM(case when month(v.fecha)=1 then V.IMP_NETO/1000 else 0 end) AS ENE
	,SUM(case when month(v.fecha)=2 then V.IMP_NETO/1000 else 0 end) AS FEB
	,SUM(case when month(v.fecha)=3 then V.IMP_NETO/1000 else 0 end) AS MAR
	,SUM(case when month(v.fecha)=4 then V.IMP_NETO/1000 else 0 end) AS ABR
	,SUM(case when month(v.fecha)=5 then V.IMP_NETO/1000 else 0 end) AS MAY
	,SUM(case when month(v.fecha)=6 then V.IMP_NETO/1000 else 0 end) AS JUN
	,SUM(case when month(v.fecha)=7 then V.IMP_NETO/1000 else 0 end) AS JUL
	,SUM(case when month(v.fecha)=8 then V.IMP_NETO/1000 else 0 end) AS AGO
	,SUM(case when month(v.fecha)=9 then V.IMP_NETO/1000 else 0 end) AS SEP
	,SUM(case when month(v.fecha)=10 then V.IMP_NETO/1000 else 0 end) AS OCT
	,SUM(case when month(v.fecha)=11 then V.IMP_NETO/1000 else 0 end) AS NOV
	,SUM(case when month(v.fecha)=12 then V.IMP_NETO/1000 else 0 end) AS DIC
	FROM fact_ventas_det v 
	where fecha BETWEEN '2016/01/01' and '2016/12/31'
	group by year(v.fecha)
		"""
		def sql = new Sql(dataSource)
		def rows = sql.rows(q)
		respond rows,[formats:['json']]
	}


	def venta(){
		//def sql = new Sql(dataSource)
    String q = """
	SELECT year(v.fecha) as "ao"
	,SUM(case when month(v.fecha)=1 then V.KILOS/1000 else 0 end) AS ENE
	,SUM(case when month(v.fecha)=2 then V.KILOS/1000 else 0 end) AS FEB
	,SUM(case when month(v.fecha)=3 then V.KILOS/1000 else 0 end) AS MAR
	,SUM(case when month(v.fecha)=4 then V.KILOS/1000 else 0 end) AS ABR
	,SUM(case when month(v.fecha)=5 then V.KILOS/1000 else 0 end) AS MAY
	,SUM(case when month(v.fecha)=6 then V.KILOS/1000 else 0 end) AS JUN
	,SUM(case when month(v.fecha)=7 then V.KILOS/1000 else 0 end) AS JUL
	,SUM(case when month(v.fecha)=8 then V.KILOS/1000 else 0 end) AS AGO
	,SUM(case when month(v.fecha)=9 then V.KILOS/1000 else 0 end) AS SEP
	,SUM(case when month(v.fecha)=10 then V.KILOS/1000 else 0 end) AS OCT
	,SUM(case when month(v.fecha)=11 then V.KILOS/1000 else 0 end) AS NOV
	,SUM(case when month(v.fecha)=12 then V.KILOS/1000 else 0 end) AS DIC
	FROM fact_ventas_det v 
	where fecha BETWEEN '2016/01/01' and '2016/12/31'
	group by year(v.fecha)
		"""
		def sql = new Sql(dataSource)
		def rows = sql.rows(q)
		respond rows,[formats:['json']]
	}


	def ventaNetoCredCont(){
		//def sql = new Sql(dataSource)
    String q = """
	SELECT year(v.fecha) as "ao"
	,SUM(case when origen='CRE' then V.IMP_NETO/1000 else 0 end) AS VTA_CRE
	,SUM(case when origen<>'CRE' then V.IMP_NETO/1000 else 0 end) AS VTA_CON
	FROM fact_ventas_det v 
	where fecha BETWEEN '2016/01/01' and '2016/12/31'
	group by year(v.fecha)
		"""
		def sql = new Sql(dataSource)
		def rows = sql.rows(q)
		respond rows,[formats:['json']]
	}
	
	def costoNetoCredCont(){
		//def sql = new Sql(dataSource)
    String q = """
	SELECT year(v.fecha) as "ao"
	,SUM(case when origen='CRE' then (V.IMP_NETO-V.COSTO_NETO)/1000 else 0 end) AS CST_CRE
	,SUM(case when origen<>'CRE' then (V.IMP_NETO-V.COSTO_NETO)/1000 else 0 end) AS CST_CON
	FROM fact_ventas_det v 
	where fecha BETWEEN '2016/01/01' and '2016/12/31'
	group by year(v.fecha)
		"""
		def sql = new Sql(dataSource)
		def rows = sql.rows(q)
		respond rows,[formats:['json']]
	}
    
}
