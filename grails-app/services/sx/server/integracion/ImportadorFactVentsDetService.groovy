package sx.server.integracion

import grails.transaction.Transactional
import groovy.sql.Sql
import org.apache.commons.lang.exception.ExceptionUtils
import sx.bi.*
import sx.Periodo


class ImportadorFactVentsDetService {

    def importadorService

    def dataSource_bi

    def dataSource_importacion

    def importarVentasCanceladasSemana(CalendarioBi calendario) {


        Sql sql = new Sql(dataSource_importacion)

        def fechaIniSem = calendario.fechaInicial

        def fechaFinSem = calendario.fechaFinal

        def mesInicial = Periodo.obtenerMes(fechaIniSem)


        def mesFinal = Periodo.obtenerMes(fechaFinSem)

        def mesFechaInicial = Periodo.getPeriodoEnUnMes(mesInicial).fechaInicial


        def mesFechaFinal = Periodo.getPeriodoEnUnMes(mesFinal).fechaFinal

        def year = Periodo.getPeriodoAnual(Periodo.obtenerYear(fechaIniSem))

        def yearFechaIni = year.fechaInicial




        if (mesInicial != mesFinal) {
            mesFechaFinal = Periodo.getPeriodoEnUnMes(mesInicial).fechaFinal
        } else {
            mesFechaFinal = fechaFinSem
        }

        sql.eachRow("""
				SELECT CONCAT(CAST(YEAR(MAX(V.FECHA)) AS CHAR(4)),'-',(CASE WHEN WEEK(MAX(V.FECHA))+1>9 THEN CAST(WEEK(MAX(V.FECHA))+1 AS CHAR(2)) ELSE CONCAT('0',CAST(WEEK(MAX(V.FECHA)) AS CHAR(2))+1) END) ) AS CALENDARIO_ID,
				'SEM' AS TIPO,
				sucursal_ID,
				(SELECT S.NOMBRE FROM sw_sucursales S WHERE (S.SUCURSAL_ID=V.SUCURSAL_ID)) AS SUCURSAL,
				COUNT(*) AS CANCELADO,
				SUM(CASE WHEN ORIGEN='CRE' THEN 1 ELSE 0 END) AS CANCELADOCRE,
				SUM(CASE WHEN ORIGEN<>'CRE' THEN 1 ELSE 0 END) AS CANCELADOCON									
				FROM sx_ventas V JOIN sx_cxc_cargos_cancelados C ON(C.CARGO_ID=V.CARGO_ID) WHERE V.FECHA BETWEEN ? AND ? AND V.TIPO='FAC'   
				GROUP BY V.SUCURSAL_ID							
				UNION									
				SELECT CONCAT(CAST(YEAR(MAX(V.FECHA)) AS CHAR(4)),'-',(CASE WHEN WEEK(MAX(V.FECHA))+1>9 THEN CAST(WEEK(MAX(V.FECHA))+1 AS CHAR(2)) ELSE CONCAT('0',CAST(WEEK(MAX(V.FECHA)) AS CHAR(2))+1) END) ) AS CALENDARIO_ID,
				'MES' AS TIPO,
				sucursal_ID,
				(SELECT S.NOMBRE FROM sw_sucursales S WHERE (S.SUCURSAL_ID=V.SUCURSAL_ID)) AS SUC,
				COUNT(*),
				SUM(CASE WHEN ORIGEN='CRE' THEN 1 ELSE 0 END) AS CANC_CRE,
				SUM(CASE WHEN ORIGEN<>'CRE' THEN 1 ELSE 0 END) AS CANC_CON									
				FROM sx_ventas V JOIN sx_cxc_cargos_cancelados C ON(C.CARGO_ID=V.CARGO_ID) WHERE V.FECHA BETWEEN ? AND ? AND V.TIPO='FAC'   
				GROUP BY V.SUCURSAL_ID									
				UNION									
				SELECT CONCAT(CAST(YEAR(MAX(V.FECHA)) AS CHAR(4)),'-',(CASE WHEN WEEK(MAX(V.FECHA))+1>9 THEN CAST(WEEK(MAX(V.FECHA))+1 AS CHAR(2)) ELSE CONCAT('0',CAST(WEEK(MAX(V.FECHA)) AS CHAR(2))+1) END) ) AS CALENDARIO_ID,
				'YEAR' AS TIPO,
				sucursal_ID,
				(SELECT S.NOMBRE FROM sw_sucursales S WHERE (S.SUCURSAL_ID=V.SUCURSAL_ID)) AS SUC,
				COUNT(*),
				SUM(CASE WHEN ORIGEN='CRE' THEN 1 ELSE 0 END) AS CANC_CRE,
				SUM(CASE WHEN ORIGEN<>'CRE' THEN 1 ELSE 0 END) AS CANC_CON									
				FROM sx_ventas V JOIN sx_cxc_cargos_cancelados C ON(C.CARGO_ID=V.CARGO_ID) WHERE V.FECHA BETWEEN ? AND ? AND V.TIPO='FAC'   
				GROUP BY V.SUCURSAL_ID
		 """, [fechaIniSem, fechaFinSem, mesFechaInicial, mesFechaFinal, yearFechaIni, fechaFinSem]) { row ->


            def canceladas = VentasCanceladasSem.findByCalendarioAndTipoAndSucursal(calendario, row.tipo, row.sucursal)
            if (canceladas) {

                canceladas.cancelado = row.cancelado
                canceladas.canceladoCre = row.canceladoCre
                canceladas.canceladoCon = row.canceladoCon
            } else {

                canceladas = new VentasCanceladasSem(
                        calendario: calendario,
                        tipo: row.tipo,
                        sucursal: row.sucursal,
                        cancelado: row.cancelado,
                        canceladoCre: row.canceladoCre,
                        canceladoCon: row.canceladoCon
                )

            }
            canceladas.save failOnError: true, flush: true


        }

    }

    def importadorFactVentasSemana(CalendarioBi calendario) {


        Sql sql = new Sql(dataSource_bi)

        def fechaIniSem = calendario.fechaInicial

        def fechaFinSem = calendario.fechaFinal

        def mesInicial = Periodo.obtenerMes(fechaIniSem)

        def mesFinal = Periodo.obtenerMes(fechaFinSem)

        def mesFechaInicial = Periodo.getPeriodoEnUnMes(mesInicial).fechaInicial

        def mesFechaFinal = Periodo.getPeriodoEnUnMes(mesFinal).fechaFinal

        def year = Periodo.getPeriodoAnual(Periodo.obtenerYear(fechaIniSem))

        def yearFechaIni = year.fechaInicial


        if (mesInicial != mesFinal) {
            mesFechaFinal = Periodo.getPeriodoEnUnMes(mesInicial).fechaFinal

        } else {
            mesFechaFinal = fechaFinSem
        }

        def query = """
							SELECT 'SEM' AS tipo ,A.suc AS sucursal,ROUND(SUM(A.KILOS)/1000,3) AS toneladas,ROUND(SUM(CASE WHEN A.ORIGEN='CRE' THEN A.KILOS ELSE 0 END)/1000,3) AS toneladasCre,ROUND(SUM(CASE WHEN A.ORIGEN<>'CRE' THEN A.KILOS ELSE 0 END)/1000,3) AS  toneladasCon
							,SUM(A.IMP_NETO) AS venta,SUM(CASE WHEN A.ORIGEN='CRE' THEN A.IMP_NETO ELSE 0 END) AS ventaCre,SUM(CASE WHEN A.ORIGEN<>'CRE' THEN A.IMP_NETO ELSE 0 END) AS ventaCon
							,SUM(CASE WHEN A.TIPO='VTA' THEN 1 ELSE 0 END) AS facturas,SUM(CASE WHEN A.TIPO='VTA' AND A.ORIGEN='CRE' THEN 1 ELSE 0 END) AS facturasCre,SUM(CASE WHEN A.TIPO='VTA' AND A.ORIGEN<>'CRE' THEN 1 ELSE 0 END) AS facturasCon
							,(SELECT COUNT(A.FECHA) FROM ( SELECT X.FECHA FROM fact_ventas_det X WHERE  X.FECHA BETWEEN @FECHA_INI_SEM AND @FECHA_FIN_SEM GROUP BY X.FECHA ) AS A ) AS dias 
							,SUM(A.COSTO) AS costo,SUM(CASE WHEN A.ORIGEN='CRE' THEN A.COSTO ELSE 0 END) AS costoCre,SUM(CASE WHEN A.ORIGEN<>'CRE' THEN A.COSTO ELSE 0 END) AS costoCon
							,SUM(CASE WHEN A.TIPO='DEV' THEN 1 ELSE 0 END) AS devoluciones,SUM(CASE WHEN A.TIPO='DEV' AND A.ORIGEN='CRE' THEN 1 ELSE 0 END) AS devolucionesCre,SUM(CASE WHEN A.TIPO='DEV' AND A.ORIGEN<>'CRE' THEN 1 ELSE 0 END) AS devolucionesCon
							,0 as canceladas,0 as canceladasCre,0 as canceladasCon,0.00 as utilidad,0.00 as utilidadPorcentaje,0.00 as utilidadCre,0.00 as utilidadPorcentajeCre,0.00 as utilidadCon,0.00 as utilidadPorcentajeCon
							FROM (
							SELECT V.TIPO,V.ORIGEN_ID,V.SUC,V.ORIGEN,YEAR(V.FECHA) AS YEAR,MONTH(V.FECHA) AS MES,WEEK(V.FECHA) AS SEM,SUM(V.KILOS) AS KILOS,SUM(V.IMP_NETO) AS IMP_NETO,SUM(V.COSTO_NETO) AS COSTO ,SUM(V.IMP_BRUTO) AS IMP_BRUTO,SUM(V.IMPORTE) AS IMPORTE
							FROM fact_ventas_det V   WHERE V.FECHA BETWEEN @FECHA_INI_SEM AND @FECHA_FIN_SEM AND V.LINEA_ID<>106 AND V.PRODUCTO_ID NOT IN(1088,5074,1087,4982)  GROUP BY V.TIPO,V.ORIGEN_ID,V.SUC,V.ORIGEN
							) AS A GROUP BY A.SUC
							UNION
							SELECT 'MES' AS TIPO ,A.SUC,ROUND(SUM(A.KILOS)/1000,3) AS TON,ROUND(SUM(CASE WHEN A.ORIGEN='CRE' THEN A.KILOS ELSE 0 END)/1000,3) AS TON_CRE,ROUND(SUM(CASE WHEN A.ORIGEN<>'CRE' THEN A.KILOS ELSE 0 END)/1000,3) AS TON_CON
							,SUM(A.IMP_NETO) AS IMP_NETO,SUM(CASE WHEN A.ORIGEN='CRE' THEN A.IMP_NETO ELSE 0 END) AS IMP_CRE,SUM(CASE WHEN A.ORIGEN<>'CRE' THEN A.IMP_NETO ELSE 0 END) AS IMP_CON
							,SUM(CASE WHEN A.TIPO='VTA' THEN 1 ELSE 0 END) AS FACS,SUM(CASE WHEN A.TIPO='VTA' AND A.ORIGEN='CRE' THEN 1 ELSE 0 END) AS FACS_CRE,SUM(CASE WHEN A.TIPO='VTA' AND A.ORIGEN<>'CRE' THEN 1 ELSE 0 END) AS FACS_CON
							,(SELECT COUNT(A.FECHA) FROM ( SELECT X.FECHA FROM fact_ventas_det X WHERE  X.FECHA BETWEEN @FECHA_INI_MES AND @FECHA_FIN_MES GROUP BY X.FECHA ) AS A ) AS DIAS 
							,SUM(A.COSTO) AS COSTO,SUM(CASE WHEN A.ORIGEN='CRE' THEN A.COSTO ELSE 0 END) AS CST_CRE,SUM(CASE WHEN A.ORIGEN<>'CRE' THEN A.COSTO ELSE 0 END) AS CST_CON
							,SUM(CASE WHEN A.TIPO='DEV' THEN 1 ELSE 0 END) AS DEVS,SUM(CASE WHEN A.TIPO='DEV' AND A.ORIGEN='CRE' THEN 1 ELSE 0 END) AS DEVS_CRE,SUM(CASE WHEN A.TIPO='DEV' AND A.ORIGEN<>'CRE' THEN 1 ELSE 0 END) AS DEVS_CON
							,0 as cancs,0 as cancs_cre,0 as cancs_con,0 as utilidad,0 as ut_porc,0 as utilidad_cre,0 as ut_porc_cre,0 as utilidad_con,0 as ut_porc_con
							FROM (
							SELECT V.TIPO,V.ORIGEN_ID,V.SUC,V.ORIGEN,YEAR(V.FECHA) AS YEAR,MONTH(V.FECHA) AS MES,WEEK(V.FECHA) AS SEM,SUM(V.KILOS) AS KILOS,SUM(V.IMP_NETO) AS IMP_NETO,SUM(V.COSTO_NETO) AS COSTO,SUM(V.IMP_BRUTO) AS IMP_BRUTO,SUM(V.IMPORTE) AS IMPORTE
							FROM fact_ventas_det V   WHERE V.FECHA BETWEEN @FECHA_INI_MES AND @FECHA_FIN_MES AND V.LINEA_ID<>106 AND V.PRODUCTO_ID NOT IN(1088,5074,1087,4982)  GROUP BY V.TIPO,V.ORIGEN_ID,V.SUC,V.ORIGEN
							) AS A GROUP BY A.SUC
							UNION
							SELECT 'YEAR' AS TIPO ,A.SUC,ROUND(SUM(A.KILOS)/1000,3) AS TON,ROUND(SUM(CASE WHEN A.ORIGEN='CRE' THEN A.KILOS ELSE 0 END)/1000,3) AS TON_CRE,ROUND(SUM(CASE WHEN A.ORIGEN<>'CRE' THEN A.KILOS ELSE 0 END)/1000,3) AS TON_CON
							,SUM(A.IMP_NETO) AS IMP_NETO,SUM(CASE WHEN A.ORIGEN='CRE' THEN A.IMP_NETO ELSE 0 END) AS IMP_CRE,SUM(CASE WHEN A.ORIGEN<>'CRE' THEN A.IMP_NETO ELSE 0 END) AS IMP_CON
							,SUM(CASE WHEN A.TIPO='VTA' THEN 1 ELSE 0 END) AS FACS,SUM(CASE WHEN A.TIPO='VTA' AND A.ORIGEN='CRE' THEN 1 ELSE 0 END) AS FACS_CRE,SUM(CASE WHEN A.TIPO='VTA' AND A.ORIGEN<>'CRE' THEN 1 ELSE 0 END) AS FACS_CON
							,(SELECT COUNT(A.FECHA) FROM ( SELECT X.FECHA FROM fact_ventas_det X WHERE  X.FECHA BETWEEN @FECHA_INI_YEAR AND @FECHA_FIN_SEM GROUP BY X.FECHA ) AS A ) AS DIAS 
							,SUM(A.COSTO) AS COSTO,SUM(CASE WHEN A.ORIGEN='CRE' THEN A.COSTO ELSE 0 END) AS CST_CRE,SUM(CASE WHEN A.ORIGEN<>'CRE' THEN A.COSTO ELSE 0 END) AS CST_CON
							,SUM(CASE WHEN A.TIPO='DEV' THEN 1 ELSE 0 END) AS DEVS,SUM(CASE WHEN A.TIPO='DEV' AND A.ORIGEN='CRE' THEN 1 ELSE 0 END) AS DEVS_CRE,SUM(CASE WHEN A.TIPO='DEV' AND A.ORIGEN<>'CRE' THEN 1 ELSE 0 END) AS DEVS_CON
							,0 as cancs,0 as cancs_cre,0 as cancs_con,0 as utilidad,0 as ut_porc,0 as utilidad_cre,0 as ut_porc_cre,0 as utilidad_con,0 as ut_porc_con
							FROM (
							SELECT V.TIPO,V.ORIGEN_ID,V.SUC,V.ORIGEN,YEAR(V.FECHA) AS YEAR,MONTH(V.FECHA) AS MES,WEEK(V.FECHA) AS SEM,SUM(V.KILOS) AS KILOS,SUM(V.IMP_NETO) AS IMP_NETO,SUM(V.COSTO_NETO) AS COSTO,SUM(V.IMP_BRUTO) AS IMP_BRUTO,SUM(V.IMPORTE) AS IMPORTE
							FROM fact_ventas_det V   WHERE V.FECHA BETWEEN  @FECHA_INI_YEAR AND @FECHA_FIN_SEM AND V.LINEA_ID<>106 AND V.PRODUCTO_ID NOT IN(1088,5074,1087,4982)  GROUP BY V.TIPO,V.ORIGEN_ID,V.SUC,V.ORIGEN
							) AS A GROUP BY A.SUC
							"""

        query = query.replaceAll('@FECHA_INI_SEM', "'" + fechaIniSem.format("yyyy/MM/dd") + "'").replaceAll('@FECHA_FIN_SEM', "'" + fechaFinSem.format("yyyy/MM/dd") + "'").replaceAll('@FECHA_INI_MES', "'" + mesFechaInicial.format("yyyy/MM/dd") + "'").replaceAll('@FECHA_FIN_MES', "'" + mesFechaFinal.format("yyyy/MM/dd") + "'").replaceAll('@FECHA_INI_YEAR', "'" + yearFechaIni.format("yyyy/MM/dd") + "'")

        sql.eachRow(query) { vent ->

            def venta = FactVentasSemBi.findByCalendarioAndTipoAndSucursal(calendario, vent.tipo, vent.sucursal)

            if (venta) {


                venta.toneladas = vent.toneladas
                venta.toneladasCre = vent.toneladasCre
                venta.toneladasCon = vent.toneladasCon
                venta.venta = vent.venta
                venta.ventaCre = vent.ventaCre
                venta.ventaCon = vent.ventaCon
                venta.facturas = vent.facturas
                venta.facturasCre = vent.facturasCre
                venta.facturasCon = vent.facturasCon
                venta.costo = vent.costo
                venta.costoCre = vent.costoCre
                venta.costoCon = vent.costoCon
                venta.devoluciones = vent.devoluciones
                venta.devolucionesCre = vent.devolucionesCre
                venta.devolucionesCon = vent.devolucionesCon
                venta.utilidad = vent.utilidad
                venta.utilidadPorcentaje = vent.utilidadPorcentaje
                venta.utilidadCre = vent.utilidadCre
                venta.utilidadPorcentajeCre = vent.utilidadPorcentajeCre
                venta.utilidadCon = vent.utilidadCon
                venta.utilidadPorcentajeCon = vent.utilidadPorcentajeCon

            } else {
                venta = new FactVentasSemBi(
                        calendario: calendario,
                        tipo: vent.tipo,
                        sucursal: vent.sucursal,
                        toneladas: vent.toneladas,
                        toneladasCre: vent.toneladasCre,
                        toneladasCon: vent.toneladasCon,
                        venta: vent.venta,
                        ventaCre: vent.ventaCre,
                        ventaCon: vent.ventaCon,
                        facturas: vent.facturas,
                        facturasCre: vent.facturasCre,
                        facturasCon: vent.facturasCon,
                        dias: vent.dias,
                        costo: vent.costo,
                        costoCre: vent.costoCre,
                        costoCon: vent.costoCon,
                        devoluciones: vent.devoluciones,
                        devolucionesCre: vent.devolucionesCre,
                        devolucionesCon: vent.devolucionesCon,
                        utilidad: vent.utilidad,
                        utilidadPorcentaje: vent.utilidadPorcentaje,
                        utilidadCre: vent.utilidadCre,
                        utilidadPorcentajeCre: vent.utilidadPorcentajeCre,
                        utilidadCon: vent.utilidadCon,
                        utilidadPorcentajeCon: vent.utilidadPorcentajeCon
                )


            }






            VentasCanceladasSem canceladas = VentasCanceladasSem.findByCalendarioAndSucursalAndTipo(calendario, venta.sucursal, venta.tipo)

            if (canceladas) {
                venta.canceladas = canceladas.cancelado
                venta.canceladasCre = canceladas.canceladoCre
                venta.canceladasCon = canceladas.canceladoCon
            }

            venta.utilidad = venta.venta - venta.costo
            if (venta.costoCre) {
                venta.utilidadCre = venta.ventaCre - venta.costoCre
            }
            if (venta.costoCon) {
                venta.utilidadCon = venta.ventaCon - venta.costoCon
            }

            venta.utilidadPorcentaje = venta.utilidad * 100 / venta.venta
            if (venta.ventaCre) {
                venta.utilidadPorcentajeCre = venta.utilidadCre * 100 / venta.ventaCre
            }
            if (venta.ventaCon) {
                venta.utilidadPorcentajeCon = venta.utilidadCon * 100 / venta.ventaCon
            }

            venta.save failOnError: true, flush: true


        }

    }

    def importadorFactVentasDet(Date fechaInicial, Date fechaFinal) {


        Sql sql = new Sql(dataSource_importacion)

        def query = """
						SELECT 'VTA' AS TIPO,D.VENTA_ID AS ORIGENID,D.INVENTARIO_ID as  inventarioId,V.CLIENTE_ID as clienteId,V.NOMBRE AS CLIENTE,V.DOCTO,V.ORIGEN,V.SUCURSAL_ID as sucursalId,(SELECT S.NOMBRE FROM sw_sucursales S WHERE S.SUCURSAL_ID=V.SUCURSAL_ID) AS suc,DATE(V.FECHA) AS FECHA
						,L.LINEA_ID as lineaId,L.NOMBRE AS LINEA,M.MARCA_ID as marcaId,M.NOMBRE AS MARCA,C.CLASE_ID as claseId,C.NOMBRE AS CLASE,P.PRODUCTO_ID as productoId,P.CLAVE,P.DESCRIPCION,P.UNIDAD,D.FACTORU,P.GRAMOS,P.KILOS AS KXMIL,P.CALIBRE,P.CARAS,P.DELINEA,P.NACIONAL
						,-D.CANTIDAD/D.FACTORU AS CANTIDAD,-D.CANTIDAD/D.FACTORU*P.KILOS AS KILOS,D.PRECIO_L as precioL,-D.CANTIDAD/D.FACTORU*D.PRECIO_L AS IMPBRUTO,(D.PRECIO_L-(D.PRECIO*V.TC))*100/D.PRECIO_L AS DSCTOESP,D.PRECIO*V.TC AS PRECIO,D.IMPORTE*V.TC AS IMPORTE,D.DSCTO,D.CORTES,D.PRECIO_CORTES as precioCortes,D.IMPORTE_NETO*V.TC AS IMPNETO,D.COSTOP
						,-D.CANTIDAD/D.FACTORU*D.COSTOP AS COSTO,(CASE WHEN P.nacional=FALSE THEN 8.00 ELSE 0.00 END) AS DESCTOCOSTO,(CASE WHEN M.NOMBRE='BURGO' THEN 1.50 WHEN  M.NOMBRE='BURGO POLART' THEN 1.50 WHEN C.NOMBRE IN('REV CAF','REV BLANCO') THEN 5.00  ELSE 0.00 END) AS REBATE,0.00 AS COSTONETO
						,(SELECT DIAID FROM DIAS F WHERE F.FECHA=V.FECHA) AS DIA_ID,1 AS SEMANA
						FROM  SX_VENTASDET D  USE INDEX (INDX_VDET2)  
						JOIN sx_ventas V ON(V.CARGO_ID=D.VENTA_ID) JOIN sx_productos P ON(P.PRODUCTO_ID=D.PRODUCTO_ID) JOIN sx_lineas L ON(L.LINEA_ID=P.LINEA_ID)JOIN sx_clases C ON(C.CLASE_ID=P.CLASE_ID)JOIN sx_marcas M ON(M.MARCA_ID=P.MARCA_ID)
						where P.PRODUCTO_ID <>5392 AND  D.fecha BETWEEN '@FECHA_INI 00:00:00' and '@FECHA_FIN 23:59:00'
						UNION
						SELECT 'DEV' AS TIPO,A.ABONO_ID AS ORIGEN_ID,D.INVENTARIO_ID,A.CLIENTE_ID,(CASE WHEN A.CLAVE=1 THEN 'MOSTRADOR' ELSE A.NOMBRE END) AS CLIENTE,A.FOLIO,A.ORIGEN,D.SUCURSAL_ID,(select s.nombre from sw_sucursales s where d.SUCURSAL_ID=s.SUCURSAL_ID) as SUC,DATE(A.FECHA) AS FECHA
						,L.LINEA_ID,L.NOMBRE AS LINEA,M.MARCA_ID,M.NOMBRE AS MARCA,C.CLASE_ID,C.NOMBRE AS CLASE,P.PRODUCTO_ID,P.CLAVE,P.DESCRIPCION,P.UNIDAD,D.FACTORU,P.GRAMOS,P.KILOS AS KXMIL,P.CALIBRE,P.CARAS,P.DELINEA,P.NACIONAL
						,-D.CANTIDAD/D.FACTORU AS CANTIDAD,-D.CANTIDAD/D.FACTORU*P.KILOS AS KILOS
						,(SELECT V.PRECIO_L FROM sx_ventasdet V WHERE V.INVENTARIO_ID=D.VENTADET_ID) AS PRECIO_L
						,(-D.CANTIDAD/D.FACTORU)*(SELECT V.PRECIO_L FROM sx_ventasdet V WHERE V.INVENTARIO_ID=D.VENTADET_ID) AS IMP_BRUTO
						,((SELECT V.PRECIO_L FROM sx_ventasdet V WHERE V.INVENTARIO_ID=D.VENTADET_ID)-(SELECT V.PRECIO*A.TC FROM sx_ventasdet V JOIN SX_VENTAS X ON(X.CARGO_ID=V.VENTA_ID) WHERE V.INVENTARIO_ID=D.VENTADET_ID))*100/(SELECT V.PRECIO_L FROM sx_ventasdet V WHERE V.INVENTARIO_ID=D.VENTADET_ID) AS DSCTO_ESP
						,(SELECT V.PRECIO*A.TC FROM sx_ventasdet V JOIN SX_VENTAS X ON(X.CARGO_ID=V.VENTA_ID) WHERE V.INVENTARIO_ID=D.VENTADET_ID) AS PRECIO
						,(-D.CANTIDAD/D.FACTORU)*(SELECT V.PRECIO*A.TC FROM sx_ventasdet V JOIN SX_VENTAS X ON(X.CARGO_ID=V.VENTA_ID) WHERE V.INVENTARIO_ID=D.VENTADET_ID) AS IMPORTE
						,(SELECT V.DSCTO FROM sx_ventasdet V WHERE V.INVENTARIO_ID=D.VENTADET_ID) AS DSCTO,(SELECT (CASE WHEN V.CANTIDAD=D.CANTIDAD THEN V.CORTES ELSE 0 END) FROM sx_ventasdet V WHERE V.INVENTARIO_ID=D.VENTADET_ID) AS CORTES,(SELECT V.PRECIO_CORTES*A.TC FROM sx_ventasdet V JOIN SX_VENTAS X ON(X.CARGO_ID=V.VENTA_ID) WHERE V.INVENTARIO_ID=D.VENTADET_ID) AS PRECIO_CORTES
						,((-D.CANTIDAD/D.FACTORU)*(SELECT V.PRECIO*A.TC FROM sx_ventasdet V JOIN SX_VENTAS X ON(X.CARGO_ID=V.VENTA_ID) WHERE V.INVENTARIO_ID=D.VENTADET_ID)*(SELECT (100-V.DSCTO)/100 FROM sx_ventasdet V WHERE V.INVENTARIO_ID=D.VENTADET_ID)) AS IMP_NETO
						,D.COSTOP,(-D.CANTIDAD/D.FACTORU*D.COSTOP) AS COSTO
						,(CASE WHEN P.nacional=FALSE THEN 8.00 ELSE 0.00 END) AS DESCTO_COSTO,(CASE WHEN M.NOMBRE='BURGO' THEN 1.50 WHEN  M.NOMBRE='BURGO POLART' THEN 1.50 WHEN C.NOMBRE IN('REV CAF','REV BLANCO') THEN 5.00  ELSE 0.00 END) AS REBATE,0.00 AS COSTO_NETO
						,(SELECT DIAID FROM DIAS F WHERE F.FECHA=A.FECHA) AS DIA_ID,1 AS SEMANA
						 FROM sx_inventario_dev D JOIN sx_devoluciones X ON(X.DEVO_ID=D.DEVO_ID) JOIN sx_cxc_abonos A ON(A.DEVOLUCION_ID=X.DEVO_ID) JOIN SX_PRODUCTOS P ON (P.PRODUCTO_ID=D.PRODUCTO_ID) JOIN sx_lineas L ON (L.LINEA_ID=P.LINEA_ID)
						 JOIN sx_clases C ON(C.CLASE_ID=P.CLASE_ID)JOIN sx_marcas M ON(M.MARCA_ID=P.MARCA_ID) 
						WHERE P.PRODUCTO_ID <>5392 AND A.FECHA BETWEEN '@FECHA_INI' AND '@FECHA_FIN'
						UNION
						SELECT 'BON' AS TIPO,A.ABONO_ID AS ORIGEN_ID,D.INVENTARIO_ID,A.CLIENTE_ID,(CASE WHEN A.CLAVE=1 THEN 'MOSTRADOR' ELSE A.NOMBRE END) AS CLIENTE,A.FOLIO,A.ORIGEN,D.SUCURSAL_ID,(select s.nombre from sw_sucursales s where d.SUCURSAL_ID=s.SUCURSAL_ID) as SUC,A.FECHA
						,L.LINEA_ID,L.NOMBRE AS LINEA,M.MARCA_ID,M.NOMBRE AS MARCA,C.CLASE_ID,C.NOMBRE AS CLASE,P.PRODUCTO_ID,P.CLAVE,P.DESCRIPCION,P.UNIDAD,D.FACTORU,P.GRAMOS,P.KILOS AS KXMIL,P.CALIBRE,P.CARAS,P.DELINEA,P.NACIONAL
						,0 AS CANTIDAD,0 AS KILOS,d.PRECIO_L,0 AS IMP_BRUTO,0 AS DSCTO_ESP,D.PRECIO*A.TC,0 AS IMPORTE
						,ROUND((SELECT AVG(n.IMPORTE*A.TC/1.16) from sx_nota_det n WHERE n.abono_id=a.abono_id)*100/(SELECT (V.IMPORTE*V.TC) from SX_VENTAS V WHERE n.venta_ID=v.cargo_ID),2) AS DSCTO,0 AS CORTES,0 AS PRECIO_CORTES
						,(ROUND(ROUND((SELECT AVG(n.IMPORTE*A.TC/1.16) from sx_nota_det n WHERE n.abono_id=a.abono_id)*100/(SELECT (V.IMPORTE*V.TC) from SX_VENTAS V WHERE n.venta_ID=v.cargo_ID),2) * -d.importe_neto*A.TC / 100 ,2)) AS IMP_NETO
						,0 AS COSTOP,0 AS COSTO,0 AS DESCTO_COSTO,0 AS REBATE,0.00 AS COSTO_NETO
						,(SELECT DIAID FROM DIAS F WHERE F.FECHA=A.FECHA) AS DIA_ID,1 AS SEMANA
						FROM  SX_VENTASDET D   JOIN sx_nota_det N ON(N.VENTA_ID=D.VENTA_ID) JOIN sx_cxc_abonos A ON(A.ABONO_ID=N.ABONO_ID) JOIN SX_PRODUCTOS P ON (P.PRODUCTO_ID=D.PRODUCTO_ID) JOIN sx_lineas L ON (L.LINEA_ID=P.LINEA_ID)  JOIN sx_clases C ON(C.CLASE_ID=P.CLASE_ID)JOIN sx_marcas M ON(M.MARCA_ID=P.MARCA_ID) 
						WHERE P.PRODUCTO_ID <>5392 AND A.ORIGEN NOT IN('CHE','JUR')  AND A.FECHA BETWEEN '@FECHA_INI' AND '@FECHA_FIN'
						"""

        query = query.replaceAll('@FECHA_INI', fechaInicial.format("yyyy/MM/dd")).replaceAll('@FECHA_FIN', fechaFinal.format("yyyy/MM/dd"))


        sql.eachRow(query) { v ->
            //	println"Importando venta para: "+v.inventarioId
            //def venta=FactVentasDet.findByInventarioId(v.inventarioId)
            def venta = FactVentasDet.find("from FactVentasDet f where  f.fecha between ? and ? and f.inventarioId=? ", [fechaInicial, fechaFinal, v.inventarioId])

            if (venta) {

                return
            }


            venta = new FactVentasDet(
                    tipo: v.tipo,
                    origenId: v.origenId,
                    inventarioId: v.inventarioId,
                    clienteId: v.clienteId,
                    cliente: v.cliente,
                    docto: v.docto,
                    origen: v.origen,
                    sucursalId: v.sucursalId,
                    suc: v.suc,
                    fecha: v.fecha,
                    lineaId: v.lineaId,
                    linea: v.linea,
                    marcaId: v.marcaId,
                    marca: v.marca,
                    claseId: v.claseId,
                    clase: v.clase,
                    productoId: v.productoId,
                    clave: v.clave,
                    descripcion: v.descripcion,
                    unidad: v.unidad,
                    factoru: v.factoru,
                    gramos: v.gramos,
                    kxmil: v.kxmil,
                    calibre: v.calibre,
                    caras: v.caras,
                    delinea: v.delinea,
                    nacional: v.nacional,
                    cantidad: v.cantidad,
                    kilos: v.kilos,
                    precioL: v.preciol,
                    impBruto: v.impBruto,
                    dsctoEsp: v.dsctoEsp,
                    precio: v.precio,
                    importe: v.importe,
                    dscto: v.dscto,
                    cortes: v.cortes,
                    precioCortes: v.precioCortes,
                    impNeto: v.impNeto,
                    costop: v.costop,
                    costo: v.costo,
                    desctoCosto: v.desctoCosto,
                    rebate: v.rebate,
                    costoNeto: v.costoNeto
                    // diaId:v.diaId,
                    //semana:v.semana

            )


            venta.save failOnError: true, flush: true

        }


    }

    def cargaInicial(Date fechaInicial, Date fechaFinal) {

        Sql sql = new Sql(dataSource_bi)

        //println "Importando ventas para FactVentasDet"

        sql.eachRow("""
				SELECT TIPO,ORIGEN_ID as origenId,INVENTARIO_ID as inventarioId,CLIENTE_ID as clienteId,CLIENTE,DOCTO,ORIGEN,SUCURSAL_ID as sucursalId
				,SUC,FECHA,LINEA_ID as lineaId,LINEA,MARCA_ID as marcaId,MARCA,CLASE_ID as claseId,CLASE,PRODUCTO_ID as productoId,CLAVE,DESCRIPCION,UNIDAD,FACTORU,GRAMOS,KXMIL,
				CALIBRE,CARAS,DELINEA,NACIONAL,CANTIDAD,KILOS,PRECIO_L as precioL,IMP_BRUTO as impBruto,DSCTO_ESP as dsctoEsp,PRECIO,IMPORTE,DSCTO,CORTES,PRECIO_CORTES as precioCortes,
				IMP_NETO as impNeto,COSTOP,COSTO,DESCTO_COSTO as desctoCosto,REBATE,COSTO_NETO as costoNeto
				FROM fact_ventasdet
				where fecha BETWEEN ? and ? 
			""", [fechaInicial, fechaFinal]) { v ->

            //println "Esta es la venta a importar: "+v.docto

            FactVentasDet venta = new FactVentasDet(
                    tipo: v.tipo,
                    origenId: v.origenId,
                    inventarioId: v.inventarioId,
                    clienteId: v.clienteId,
                    cliente: v.cliente,
                    docto: v.docto,
                    origen: v.origen,
                    sucursalId: v.sucursalId,
                    suc: v.suc,
                    fecha: v.fecha,
                    lineaId: v.lineaId,
                    linea: v.linea,
                    marcaId: v.marcaId,
                    marca: v.marca,
                    claseId: v.claseId,
                    clase: v.clase,
                    productoId: v.productoId,
                    clave: v.clave,
                    descripcion: v.descripcion,
                    unidad: v.unidad,
                    factoru: v.factoru,
                    gramos: v.gramos,
                    kxmil: v.kxmil,
                    calibre: v.calibre,
                    caras: v.caras,
                    delinea: v.delinea,
                    nacional: v.nacional,
                    cantidad: v.cantidad,
                    kilos: v.kilos,
                    precioL: v.preciol,
                    impBruto: v.impBruto,
                    dsctoEsp: v.dsctoEsp,
                    precio: v.precio,
                    importe: v.importe,
                    dscto: v.dscto,
                    cortes: v.cortes,
                    precioCortes: v.precioCortes,
                    impNeto: v.impNeto,
                    costop: v.costop,
                    costo: v.costo,
                    desctoCosto: v.desctoCosto,
                    rebate: v.rebate,
                    costoNeto: v.costoNeto
                    // diaId:v.diaId,
                    //semana:v.semana

            )

            importadorService.persist(venta)


        }

    }

    def importadorMargenAnualLinea(CalendarioBi calendario) {

        Sql sql = new Sql(dataSource_bi)

        def year = Periodo.obtenerYear(calendario.fechaFinal)
        def fechaInicial = Periodo.getPeriodoAnual(year).fechaInicial

        String query = """
					SELECT 'DELINEA' as tipo,concat(LINEA,'-',(case when (linea_id=114 and marca_id=164)  then concat(MARCA,'-')  when (linea_id=122 and marca_id=102)  then concat(MARCA,'-') when linea_id=120 OR marca_id in(100,206,109,106,216) then concat(MARCA,'-') when clase_id=223 then concat(CLASE,'-')  else '' end ),(case when nacional is true then 'NAL' else 'IMP' END)) AS LINEA
					,SUM(IMP_NETO) AS VENTA,SUM(COSTO_NETO) AS COSTO,SUM(IMP_NETO)-SUM(COSTO_NETO) AS UTILIDAD
					FROM fact_ventas_det f where f.FECHA BETWEEN '@FECHA_INI' and '@FECHA_FIN'  AND F.LINEA_ID<>106  and DELINEA is true 
					GROUP BY concat(LINEA,'-',(case when (linea_id=114 and marca_id=164)  then concat(MARCA,'-')  when (linea_id=122 and marca_id=102)  then concat(MARCA,'-') when linea_id=120 OR marca_id in(100,206,109,106,216) then concat(MARCA,'-') when clase_id=223 then concat(CLASE,'-')  else '' end ),(case when nacional is true then 'NAL' else 'IMP' END))
					union
					SELECT 'ESPECIAL' as tipo,concat(LINEA,'-',(case when (linea_id=114 and marca_id=164)  then concat(MARCA,'-')  when (linea_id=122 and marca_id=102)  then concat(MARCA,'-') when linea_id=120 OR marca_id in(100,206,109,106,216) then concat(MARCA,'-') when clase_id=223 then concat(CLASE,'-')  else '' end ),(case when nacional is true then 'NAL' else 'IMP' END)) AS LINEA
					,SUM(IMP_NETO) AS VENTA,SUM(COSTO_NETO) AS COSTO,SUM(IMP_NETO)-SUM(COSTO_NETO) AS UTILIDAD
					FROM fact_ventas_det f where f.FECHA BETWEEN '@FECHA_INI' and '@FECHA_FIN' AND F.LINEA_ID<>106  and DELINEA is false 
					GROUP BY concat(LINEA,'-',(case when (linea_id=114 and marca_id=164)  then concat(MARCA,'-')  when (linea_id=122 and marca_id=102)  then concat(MARCA,'-') when linea_id=120 OR marca_id in(100,206,109,106,216) then concat(MARCA,'-') when clase_id=223 then concat(CLASE,'-')  else '' end ),(case when nacional is true then 'NAL' else 'IMP' END))
					"""

        query = query.replaceAll('@FECHA_INI', fechaInicial.format("yyyy/MM/dd")).replaceAll('@FECHA_FIN', calendario.fechaFinal.format("yyyy/MM/dd"))

        //println query

        sql.eachRow(query) { m ->


            def margen = MargenAnualLinea.where { calendario == calendario && linea == m.linea }.find()

            if (margen) {

                margen.venta = m.venta
                margen.costo = m.costo
                margen.utilidad = m.utilidad
            } else {

                margen = new MargenAnualLinea(
                        calendario: calendario,
                        tipo: m.tipo,
                        linea: m.linea,
                        venta: m.venta,
                        costo: m.costo,
                        utilidad: m.utilidad
                )
            }



            margen.save failOnError: true, flush: true


        }

    }

    def importadorInventarioAlcance(CalendarioBi calendario) {


        Sql sql = new Sql(dataSource_importacion)

        def query = """SELECT x.deLinea,x.linea,0.00 AS margenPorcentajeUt
								,SUM(case when x.delinea is false then x.ex_ton when x.alc_tot>=6 then x.ex_ton else 0 end) as mayorToneladas,0 as mayorParticipacion
								,sum(case when x.delinea is false then 1 when x.alc_tot>=6 then 1 else 0 end) as mayorProductos
								,SUM(case when x.delinea is false then x.costo when x.alc_tot>=6 then x.costo else 0 end) as mayorCosto
								,SUM(case when x.delinea is true and x.alc_tot<6 then x.ex_ton else 0 end) as menorToneladas,0 as menorParticipacion
								,sum(case when x.delinea is true and x.alc_tot<6 then 1 else 0 end) as menorProductos
								,SUM(case when x.delinea is true and x.alc_tot<6 then x.costo else 0 end) as menorCosto
								,sum(case when x.delinea is true and x.alc_tot<=1 and x.nacional is true then 1 else 0 end) as menorDias1nal
								,sum(case when x.delinea is true and x.alc_tot<=0.5 and x.nacional is true then 1 else 0 end) as menorDias2nal
								,sum(case when x.delinea is true and x.alc_tot<=1 and x.nacional is false then 1 else 0 end) as menorDias1imp
								,sum(case when x.delinea is true and x.alc_tot<=0.5 and x.nacional is false then 1 else 0 end) as menorDias2imp
								FROM ( 
								SELECT P.DELINEA as deLinea,P.clave,P.descripcion,p.nacional,concat(L.NOMBRE,'-',(case when (L.linea_id=114 and M.marca_id=164)  then concat(M.NOMBRE,'-')  when (L.linea_id=122 and M.marca_id=102)  then concat(M.NOMBRE,'-') when L.linea_id=120 OR M.marca_id in(100,206,109,106,216) then concat(M.NOMBRE,'-') when C.clase_id=223 then concat(C.NOMBRE,'-')  else '' end ),(case when P.nacional is true then 'NAL' else 'IMP' END)) AS linea
								,SUM(EXI*P.KILOS/1000) AS ex_ton
								,SUM(A.COSTO) AS costo
								,IFNULL(IFNULL(SUM(EXI),0)/IFNULL((CASE WHEN SUM(VTA+DEV)=0 AND P.UNIDAD='MIL' THEN 0.100 WHEN SUM(VTA+DEV)=0 AND P.UNIDAD<>'MIL' THEN 1 ELSE
								 (SUM(VTA+DEV)/(ROUND((ROUND(TO_DAYS('@FECHA_FIN 23:59:00') -TO_DAYS('@FECHA_INI 00:00:00'),0)),0)/30.4166)) END),0),0) AS alc_tot
								FROM (
								SELECT X.PRODUCTO_ID,X.CLAVE,SUM(X.CANTIDAD/X.FACTORU) AS EXI,0 AS VTA,0 AS DEV,0 AS PEND,X.SUCURSAL_ID
										,SUM(X.CANTIDAD/X.FACTORU)*(SELECT Y.COSTOP FROM sx_costos_p Y WHERE Y.YEAR=YEAR('@FECHA_FIN') AND Y.MES=MONTH('@FECHA_FIN') AND Y.PRODUCTO_ID=X.PRODUCTO_ID) AS COSTO
									FROM SX_EXISTENCIAS X WHERE X.YEAR=YEAR('@FECHA_FIN 23:59:00') AND  X.MES=MONTH('@FECHA_FIN 23:59:00') AND  X.SUCURSAL_ID LIKE '%' GROUP BY X.CLAVE,X.PRODUCTO_ID
								UNION
								SELECT X.PRODUCTO_ID,X.CLAVE,0,SUM(-X.CANTIDAD/X.FACTORU),0,0,X.SUCURSAL_ID,0 AS COSTO FROM SX_VENTASDET X WHERE X.FECHA BETWEEN '@FECHA_INI 00:00:00' AND  '@FECHA_FIN 23:59:00' AND X.SUCURSAL_ID LIKE '%' GROUP BY X.CLAVE,X.PRODUCTO_ID
								UNION
								SELECT X.PRODUCTO_ID,X.CLAVE,0,0,SUM(-X.CANTIDAD/X.FACTORU),0,X.SUCURSAL_ID,0 AS COSTO FROM sx_inventario_dev X WHERE X.FECHA BETWEEN '@FECHA_INI 00:00:00' AND  '@FECHA_FIN 23:59:00' AND X.SUCURSAL_ID LIKE '%' GROUP BY X.CLAVE,X.PRODUCTO_ID
								) AS A
								JOIN sx_productos P ON(A.PRODUCTO_ID=P.PRODUCTO_ID)
								join sx_LINEAS L on(L.LINEA_ID=p.LINEA_ID)
								JOIN SX_CLASES C ON(C.CLASE_ID=P.CLASE_ID)
								JOIN sx_marcas M ON(M.MARCA_ID=P.MARCA_ID)
								WHERE P.ACTIVO IS TRUE AND P.INVENTARIABLE IS TRUE
								GROUP BY P.DELINEA,P.CLAVE,P.DESCRIPCION,concat(L.NOMBRE,'-',(case when (L.linea_id=114 and M.marca_id=164)  then concat(M.NOMBRE,'-')  when (L.linea_id=122 and M.marca_id=102)  then concat(M.NOMBRE,'-') when L.linea_id=120 OR M.marca_id in(100,206,109,106,216) then concat(M.NOMBRE,'-') when C.clase_id=223 then concat(C.NOMBRE,'-')  else '' end ),(case when P.nacional is true then 'NAL' else 'IMP' END))
								) AS X
								group by x.deLinea,x.linea							
						"""

        def fechaFinal = calendario.fechaFinal

        def yearAnt = Periodo.obtenerYear(calendario.fechaFinal) - 1
        def mesFecha = Periodo.obtenerMes(calendario.fechaFinal) + 1
        def diaFecha = Periodo.obtenerDia(calendario.fechaFinal)




        def fechaInicial = yearAnt + "/" + mesFecha + "/" + diaFecha

        query = query.replaceAll('@FECHA_INI', fechaInicial).replaceAll('@FECHA_FIN', fechaFinal.format("yyyy/MM/dd"))

        log.info query

        def total = 0.0

        def inventarios = InventarioSucursal.findAllByCalendario(calendario)
        inventarios.each { inv ->


            total = total + inv.toneladas

        }
        sql.eachRow(query) { i ->

            InventarioAlcance inventario = InventarioAlcance.findByLineaAndCalendario(i.linea, calendario)

            if (inventario) {
                inventario.margenPorcentajeUt = i.margenPorcentajeUt
                inventario.mayorToneladas = i.mayorToneladas
                inventario.mayorParticipacion = i.mayorParticipacion
                inventario.mayorProductos = i.mayorProductos
                inventario.mayorCosto = i.mayorCosto
                inventario.menorToneladas = i.menorToneladas
                inventario.menorParticipacion = i.menorParticipacion
                inventario.menorProductos = i.menorProductos
                inventario.menorCosto = i.menorCosto
                inventario.menorDias1Nal = i.menorDias1Nal
                inventario.menorDias2Nal = i.menorDias2Nal
                inventario.menorDias1Imp = i.menorDias1Imp
                inventario.menorDias2Imp = i.menorDias2Imp
                inventario.mayorParticipacion = total ? (inventario.mayorToneladas * 100) / total : 0
                inventario.menorParticipacion = total ? (inventario.menorToneladas * 100) / total : 0
            } else {
                inventario = new InventarioAlcance(
                        calendario: calendario,
                        linea: i.linea,
                        margenPorcentajeUt: i.margenPorcentajeUt,
                        mayorToneladas: i.mayorToneladas,
                        mayorParticipacion: i.mayorParticipacion,
                        mayorProductos: i.mayorProductos,
                        mayorCosto: i.mayorCosto,
                        menorToneladas: i.menorToneladas,
                        menorParticipacion: i.menorParticipacion,
                        menorProductos: i.menorProductos,
                        menorCosto: i.menorCosto,
                        menorDias1Nal: i.menorDias1Nal,
                        menorDias2Nal: i.menorDias2Nal,
                        menorDias1Imp: i.menorDias1Imp,
                        menorDias2Imp: i.menorDias2Imp,
                        deLinea: i.deLinea
                )

                inventario.mayorParticipacion = total ? (inventario.mayorToneladas * 100) / total : 0
                inventario.menorParticipacion = total ? (inventario.menorToneladas * 100) / total : 0

            }

            def tipo

            if (inventario.deLinea) {
                tipo = "DELINEA"
            } else {
                tipo = "ESPECIAL"
            }

            MargenAnualLinea margen = MargenAnualLinea.findByCalendarioAndLineaAndTipo(calendario, inventario.linea, tipo)

            if (margen) {


                inventario.margenPorcentajeUt = (margen.utilidad * 100) / margen.venta


            }
            inventario.save failOnError: true, flush: true

        }

    }

    def importadorInventarioSucursal(CalendarioBi calendario) {


        def year = Periodo.obtenerYear(calendario.fechaFinal)
        def mes = Periodo.obtenerMes(calendario.fechaFinal) + 1


        Sql sql = new Sql(dataSource_importacion)



        String query = """SELECT  S.NOMBRE AS almacen,SUM((E.CANTIDAD/E.FACTORU)*P.KILOS)/1000 AS toneladas
						,SUM( (E.CANTIDAD/E.FACTORU)* (ifnull((case when ifnull((SELECT x.COSTOP FROM sx_costos_p x WHERE P.PRODUCTO_ID=x.PRODUCTO_ID AND x.YEAR=@YEAR AND x.MES=@MES ) ,0)=0 then 
						ifnull((SELECT x.COSTOP FROM sx_costos_p x WHERE P.PRODUCTO_ID=x.PRODUCTO_ID AND x.YEAR=@YEAR AND x.MES=@MES-1 ) ,0) else ifnull((SELECT x.COSTOP FROM sx_costos_p x WHERE P.PRODUCTO_ID=x.PRODUCTO_ID AND x.YEAR=@YEAR AND x.MES=@MES ) ,0)
					 	end),0))   ) AS COSTO
						FROM sx_existencias E JOIN sw_sucursales S ON(S.SUCURSAL_ID=E.SUCURSAL_ID) JOIN sx_productos P ON(P.PRODUCTO_ID=E.PRODUCTO_ID)
						WHERE YEAR=@YEAR AND MES=@MES AND P.INVENTARIABLE IS TRUE GROUP BY E.SUCURSAL_ID
					"""

        query = query.replaceAll('@YEAR', year.toString()).replaceAll('@MES', mes.toString())



        def total = 0.0

        sql.eachRow(query) { i ->


            InventarioSucursal inventario = InventarioSucursal.findByCalendarioAndAlmacen(calendario, i.almacen)

            if (inventario) {
                inventario.toneladas = i.toneladas
                inventario.costo = i.costo
            } else {
                inventario = new InventarioSucursal(

                        calendario: calendario,
                        toneladas: i.toneladas,
                        almacen: i.almacen,
                        costo: i.costo

                )
            }



            inventario.save failOnError: true, flush: true
            total = total + inventario.toneladas
        }

        def inventarios = InventarioSucursal.findAllByCalendario(calendario)
        inventarios.each { inv ->


            inv.participacion = (inv.toneladas * 100) / total
            inv.save failOnError: true, flush: true


        }


    }


    def importadorEntradaComprasSem(CalendarioBi calendario) {


        Sql sql = new Sql(dataSource_importacion)

        def fechaFinal = calendario.fechaFinal.format("yyyy/MM/dd")

        String query = """
						SELECT 
						sum(round((i.cantidad/i.factoru*p.kilos),0)) as kilos
						,sum(case when p.nacional is true then round((i.cantidad/i.factoru*p.kilos),0) else 0 end)/1000 as kilosNal
						,sum(case when p.nacional is false then round((i.cantidad/i.factoru*p.kilos),0) else 0 end)/1000 as kilosImp
						FROM sx_inventario_com i join sx_productos p on(p.PRODUCTO_ID=i.PRODUCTO_ID)
						where i.FECHA BETWEEN DATE(DATE_ADD('@FECHA_FIN 00:00:00', INTERVAL -6 DAY)) AND '@FECHA_FIN 23:00:00' 
						"""

        query = query.replaceAll('@FECHA_FIN', fechaFinal)

        //println query

        sql.eachRow(query) { e ->

            EntradaComprasSem entrada = EntradaComprasSem.findByCalendario(calendario)
            if (entrada) {


                entrada.kilos = e.kilos
                entrada.kilosNal = e.kilosNal
                entrada.kilosImp = e.kilosImp
            } else {

                entrada = new EntradaComprasSem(
                        calendario: calendario,
                        kilos: e.kilos,
                        kilosNal: e.kilosNal,
                        kilosImp: e.kilosImp
                )
            }




            entrada.save failOnError: true, flush: true


        }

    }


    def importadorJuridico(CalendarioBi calendario) {

        Sql sql = new Sql(dataSource_importacion)

        def fechaFinal = calendario.fechaFinal.format("yyyy/MM/dd")

        String query = """SELECT X.CLIENTE ,X.NOMBRE,TRASPASO,SUM(SALDO) AS SALDO
						,IFNULL(SUM((SELECT SUM(XX.TOTAL) FROM sx_cxc_abonos XX WHERE XX.CLIENTE_ID=X.CLIENTE_ID AND XX.ORIGEN='JUR' AND XX.TIPO_ID<>'NOTA_BON' 		
							AND YEAR(XX.SAF)=(CASE WHEN MONTH('@FECHA_FIN') IN (1,2) THEN YEAR('@FECHA_FIN')-1 ELSE YEAR('@FECHA_FIN') END) AND MONTH(XX.SAF)= (CASE WHEN MONTH('@FECHA_FIN')=1 THEN 11  WHEN MONTH('@FECHA_FIN')=2 THEN 12 ELSE MONTH('@FECHA_FIN')-2 END)	
								)),0) AS MES1
						,IFNULL(SUM((SELECT SUM(XX.TOTAL) FROM sx_cxc_abonos XX WHERE XX.CLIENTE_ID=X.CLIENTE_ID AND XX.ORIGEN='JUR' AND XX.SAF IS NOT NULL AND XX.TIPO_ID<>'NOTA_BON' 		
							AND YEAR(XX.SAF)=(CASE WHEN MONTH('@FECHA_FIN')=1 THEN YEAR('@FECHA_FIN')-1 ELSE YEAR('@FECHA_FIN') END) AND MONTH(XX.SAF)= (CASE WHEN MONTH('@FECHA_FIN')=1 THEN 12  ELSE MONTH('@FECHA_FIN')-1 END))),0) AS MES2	
						,IFNULL(SUM((SELECT SUM(XX.TOTAL) FROM sx_cxc_abonos XX WHERE XX.CLIENTE_ID=X.CLIENTE_ID AND XX.ORIGEN='JUR' AND XX.SAF IS NOT NULL AND XX.TIPO_ID<>'NOTA_BON' AND MONTH(XX.SAF)=MONTH('@FECHA_FIN') AND YEAR(XX.SAF)=YEAR('@FECHA_FIN'))),0) AS MES3
						,IFNULL(MAX((SELECT (XX.TOTAL) FROM sx_cxc_abonos XX WHERE XX.CLIENTE_ID=X.CLIENTE_ID AND XX.ORIGEN='JUR' AND XX.SAF<='@FECHA_FIN' AND XX.TIPO_ID<>'NOTA_BON' ORDER BY XX.SAF DESC limit 1)),0) AS PAGO
						,DATE(MAX((SELECT MAX(XX.SAF) FROM sx_cxc_abonos XX WHERE XX.CLIENTE_ID=X.CLIENTE_ID AND XX.ORIGEN='JUR' AND XX.SAF<='@FECHA_FIN'  AND XX.TIPO_ID<>'NOTA_BON' ORDER BY XX.SAF DESC limit 1))) AS ultimoPago,abogado
						FROM (
						SELECT V.CLIENTE_ID,V.CLAVE as cliente,V.nombre
						,SUM(V.TOTAL-IFNULL((SELECT SUM(B.IMPORTE) FROM sx_cxc_aplicaciones B WHERE B.CARGO_ID=V.CARGO_ID AND B.fecha<='@FECHA_FIN'),0)) AS SALDO 
						,IFNULL((SELECT ABOGADO FROM SX_JURIDICO J WHERE J.CARGO_ID=V.CARGO_ID ),'SIN ASIGNAR A UN ABOGADO') AS ABOGADO
						,(SELECT TRASPASO FROM SX_JURIDICO J WHERE J.CARGO_ID=V.CARGO_ID ) AS TRASPASO
						FROM sx_ventas V 
						WHERE (SELECT J.TRASPASO  FROM SX_JURIDICO J WHERE V.CARGO_ID=J.CARGO_ID) <='@FECHA_FIN' AND V.CLAVE LIKE '%' AND V.CARGO_ID IN (SELECT J.cargo_id FROM sx_juridico J where V.CARGO_ID=J.CARGO_ID) 
						AND (SELECT ABOGADO FROM SX_JURIDICO J WHERE J.CARGO_ID=V.CARGO_ID )LIKE '%'
						GROUP BY V.CLAVE,ABOGADO HAVING SUM(V.TOTAL-IFNULL((SELECT SUM(B.IMPORTE) FROM sx_cxc_aplicaciones B WHERE B.CARGO_ID=V.CARGO_ID AND B.fecha<='@FECHA_FIN'),0))<>0
						UNION
						SELECT V.CLIENTE_ID,V.CLAVE AS CLIENTE,V.NOMBRE
						,SUM(-V.TOTAL+v.diferencia+IFNULL((SELECT SUM(B.IMPORTE) FROM sx_cxc_aplicaciones B WHERE B.ABONO_ID=V.ABONO_ID AND B.fecha<='@FECHA_FIN'),0)) AS SALDO 
						,'' AS ABOGADO
						,null AS TRASPASO
						FROM sx_cxc_abonos V 
						WHERE  V.FECHA <='@FECHA_FIN' AND V.CLAVE LIKE '%' AND V.ORIGEN='JUR' AND V.TIPO_ID LIKE 'NOTA%' 
						AND V.TOTAL-v.diferencia-IFNULL((SELECT SUM(B.IMPORTE) FROM sx_cxc_aplicaciones B WHERE B.ABONO_ID=V.ABONO_ID AND B.fecha<='@FECHA_FIN'),0)<>0
						GROUP BY V.CLAVE HAVING SUM(-V.TOTAL+v.diferencia+IFNULL((SELECT SUM(B.IMPORTE) FROM sx_cxc_aplicaciones B WHERE B.ABONO_ID=V.ABONO_ID AND B.fecha<='@FECHA_FIN'),0))<>0
						) X  
						GROUP BY X.CLIENTE
						ORDER BY X.TRASPASO,X.ABOGADO,X.CLIENTE
					"""

        query = query.replaceAll('@FECHA_FIN', fechaFinal)



        sql.eachRow(query) { j ->


            Juridico juridico = Juridico.findByCalendarioAndCliente(calendario, j.cliente)
            if (juridico) {
                juridico.saldo = j.saldo
                juridico.ultimoPago = j.ultimoPago
                juridico.pago = j.pago
                juridico.abogado = j.abogado
                juridico.mes1 = j.mes1
                juridico.mes2 = j.mes2
                juridico.mes3 = j.mes3

            } else {
                juridico = new Juridico(
                        cliente: j.cliente,
                        nombre: j.nombre,
                        traspaso: j.traspaso,
                        saldo: j.saldo,
                        ultimoPago: j.ultimoPago,
                        pago: j.pago,
                        abogado: j.abogado,
                        mes1: j.mes1,
                        mes2: j.mes2,
                        mes3: j.mes3,
                        calendario: calendario
                )
            }

            juridico.save(failOnError: true, flush: true)


        }

    }

    def borrarCancelaciones(Date fecha) {

        Sql sql = new Sql(dataSource_importacion)

        def query = """SELECT v.cargo_id,v.fecha
                        FROM  sx_ventas v join sx_cxc_cargos_cancelados c on (v.CARGO_ID=c.CARGO_ID)
                        where v.FECHA<>date(c.fecha) and date(c.fecha)=?
                        order by c.fecha desc
						"""
        sql.eachRow(query, [fecha]) { v ->

            def cancelada = FactVentasDet.where { fecha == v.fecha && origenId == v.cargo_id }.find()
            if (cancelada) {
                println "Venta encontrada procediendo a su cancelacion:  " + v.cargo_id
                cancelada.delete()
                sql.execute("delete from fact_ventasdet where origen_id = ?", [v.cargo_id])
            }
        }
    }


}