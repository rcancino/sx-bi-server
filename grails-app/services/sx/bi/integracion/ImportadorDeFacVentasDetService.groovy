package sx.bi.integracion

import grails.transaction.NotTransactional
import grails.transaction.Transactional
import groovy.sql.Sql
import org.apache.commons.lang.exception.ExceptionUtils
import sx.Periodo
import sx.bi.CalendarioBi
import sx.bi.FactVentasDet
import sx.bi.Mes


class ImportadorDeFacVentasDetService {

    def dataSource_importacion

    def importar(Mes mes){
        Periodo periodo = Periodo.getPeriodoEnUnMes(mes.getId() - 1)
        importar(periodo.fechaInicial, periodo.fechaFinal)

    }

    @NotTransactional
    def importar(Date fechaIni, fechaFin){
        (fechaIni..fechaFin).each{ fecha ->
            importar(fecha)
        }
    }

    @NotTransactional
    def importar(Date fecha) {
        Sql sql = new Sql(dataSource_importacion)
        eliminarRegistros(fecha)
        String query = SQL
                .replaceAll('@FECHA', fecha.format("yyyy/MM/dd"))
        println query
        sql.eachRow(query) { v ->
            try {
                def entity = toEntity(v)
                entity.save failOnError: true, flush: true
            }catch (Exception ex){
                String msg = ExceptionUtils.getRootCauseMessage(ex)
                log.error('Error salvando entidad: '+msg,ex)
            }
        }
    }



    def toEntity(def v){
        def venta = new FactVentasDet(
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

        )
        return venta
    }

    def eliminarRegistros(Date fecha){
        def deleted = FactVentasDet
                .executeUpdate("delete from FactVentasDet where date(fecha) = ?",[fecha])
        return deleted
    }

    static  SQL = """
        SELECT 'VTA' AS TIPO,D.VENTA_ID AS ORIGENID,D.INVENTARIO_ID as  inventarioId,V.CLIENTE_ID as clienteId,V.NOMBRE AS CLIENTE,V.DOCTO,V.ORIGEN,V.SUCURSAL_ID as sucursalId,(SELECT S.NOMBRE FROM sw_sucursales S WHERE S.SUCURSAL_ID=V.SUCURSAL_ID) AS suc,DATE(V.FECHA) AS FECHA
        ,L.LINEA_ID as lineaId,L.NOMBRE AS LINEA,M.MARCA_ID as marcaId,M.NOMBRE AS MARCA,C.CLASE_ID as claseId,C.NOMBRE AS CLASE,P.PRODUCTO_ID as productoId,P.CLAVE,P.DESCRIPCION,P.UNIDAD,D.FACTORU,P.GRAMOS,P.KILOS AS KXMIL,P.CALIBRE,P.CARAS,P.DELINEA,P.NACIONAL
        ,-D.CANTIDAD/D.FACTORU AS CANTIDAD,-D.CANTIDAD/D.FACTORU*P.KILOS AS KILOS,D.PRECIO_L as precioL,-D.CANTIDAD/D.FACTORU*D.PRECIO_L AS IMPBRUTO,(D.PRECIO_L-(D.PRECIO*V.TC))*100/D.PRECIO_L AS DSCTOESP,D.PRECIO*V.TC AS PRECIO,D.IMPORTE*V.TC AS IMPORTE,D.DSCTO,D.CORTES,D.PRECIO_CORTES as precioCortes,D.IMPORTE_NETO*V.TC AS IMPNETO,D.COSTOP
        ,-D.CANTIDAD/D.FACTORU*D.COSTOP AS COSTO,(CASE WHEN P.nacional=FALSE THEN 8.00 ELSE 0.00 END) AS DESCTOCOSTO,(CASE WHEN M.NOMBRE='BURGO' THEN 1.50 WHEN  M.NOMBRE='BURGO POLART' THEN 1.50 WHEN C.NOMBRE IN('REV CAF','REV BLANCO') THEN 5.00  ELSE 0.00 END) AS REBATE,0.00 AS COSTONETO
        ,(SELECT DIAID FROM DIAS F WHERE F.FECHA=V.FECHA) AS DIA_ID,1 AS SEMANA
        FROM  SX_VENTASDET D  USE INDEX (INDX_VDET2)
        JOIN sx_ventas V ON(V.CARGO_ID=D.VENTA_ID) JOIN sx_productos P ON(P.PRODUCTO_ID=D.PRODUCTO_ID) JOIN sx_lineas L ON(L.LINEA_ID=P.LINEA_ID)JOIN sx_clases C ON(C.CLASE_ID=P.CLASE_ID)JOIN sx_marcas M ON(M.MARCA_ID=P.MARCA_ID)
        where P.PRODUCTO_ID <>5392 AND   D.fecha BETWEEN '@FECHA 00:00:00' and '@FECHA 23:59:00'
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
        WHERE P.PRODUCTO_ID <>5392 AND A.FECHA = '@FECHA'
        UNION
        SELECT 'BON' AS TIPO,A.ABONO_ID AS ORIGEN_ID,D.INVENTARIO_ID,A.CLIENTE_ID,(CASE WHEN A.CLAVE=1 THEN 'MOSTRADOR' ELSE A.NOMBRE END) AS CLIENTE,A.FOLIO,A.ORIGEN,D.SUCURSAL_ID,(select s.nombre from sw_sucursales s where d.SUCURSAL_ID=s.SUCURSAL_ID) as SUC,A.FECHA
        ,L.LINEA_ID,L.NOMBRE AS LINEA,M.MARCA_ID,M.NOMBRE AS MARCA,C.CLASE_ID,C.NOMBRE AS CLASE,P.PRODUCTO_ID,P.CLAVE,P.DESCRIPCION,P.UNIDAD,D.FACTORU,P.GRAMOS,P.KILOS AS KXMIL,P.CALIBRE,P.CARAS,P.DELINEA,P.NACIONAL
        ,0 AS CANTIDAD,0 AS KILOS,d.PRECIO_L,0 AS IMP_BRUTO,0 AS DSCTO_ESP,D.PRECIO*A.TC,0 AS IMPORTE
        ,ROUND((SELECT AVG(n.IMPORTE*A.TC/1.16) from sx_nota_det n WHERE n.abono_id=a.abono_id)*100/(SELECT (V.IMPORTE*V.TC) from SX_VENTAS V WHERE n.venta_ID=v.cargo_ID),2) AS DSCTO,0 AS CORTES,0 AS PRECIO_CORTES
        ,(ROUND(ROUND((SELECT AVG(n.IMPORTE*A.TC/1.16) from sx_nota_det n WHERE n.abono_id=a.abono_id)*100/(SELECT (V.IMPORTE*V.TC) from SX_VENTAS V WHERE n.venta_ID=v.cargo_ID),2) * -d.importe_neto*A.TC / 100 ,2)) AS IMP_NETO
        ,0 AS COSTOP,0 AS COSTO,0 AS DESCTO_COSTO,0 AS REBATE,0.00 AS COSTO_NETO
        ,(SELECT DIAID FROM DIAS F WHERE F.FECHA=A.FECHA) AS DIA_ID,1 AS SEMANA
        FROM  SX_VENTASDET D   JOIN sx_nota_det N ON(N.VENTA_ID=D.VENTA_ID) JOIN sx_cxc_abonos A ON(A.ABONO_ID=N.ABONO_ID) JOIN SX_PRODUCTOS P ON (P.PRODUCTO_ID=D.PRODUCTO_ID) JOIN sx_lineas L ON (L.LINEA_ID=P.LINEA_ID)  JOIN sx_clases C ON(C.CLASE_ID=P.CLASE_ID)JOIN sx_marcas M ON(M.MARCA_ID=P.MARCA_ID)
        WHERE P.PRODUCTO_ID <>5392 AND A.ORIGEN NOT IN('CHE','JUR')  AND A.FECHA = '@FECHA'
	"""
}
