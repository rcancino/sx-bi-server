package sx.server

class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }
        
        "/api/bi/calendarios"(resources:'calendarioBi')
        
        "/api/bi/papelKpis"(resources:'papelKpi')

        "/api/bi/papelKpiInventarios"(resources:'papelKpiInventario')

        "/api/bi/papelKpiToneladas"(resources:'papelKpiToneladas')

        "/api/bi/clienteAtrasoMax"(resources:'clienteAtrasoMax')

        "/api/bi/factVentasSemBi"(resources:'factVentasSemBi')

        "/api/bi/inventarioSucursal"(resources:'inventarioSucursal')

        "/api/bi/pedidoPorLlegar"(resources:'pedidoPorLlegar')

        "/api/bi/inventarioAlcance"(resources:'inventarioAlcance')

        "/api/bi/calificaciones"(resources:'calificacion'){
            "/partidas"(controller:'calificacion', action: 'findPartidas', method: 'GET')
        }
        
        "/api/bi/inventarioMargenSemanal"(controller:'inventarioAlcance', action:'margenSemanal', method:'GET')

        "/api/bi/comprobacionDeInventario"(
            controller:'inventarioSucursal', 
            action:'comprobacionDeInventario', 
            method:'GET')

        "/api/bi/juridico"(resources:'juridico')
        
        "/api/bi/estadisticas/comparativoCalificacion"(controller:'estadisticas', action: 'comparativoCalificacion', method: 'GET')

        "/api/bi/analisis/margenPorcentaje"(controller:'estadisticas', action: 'margenPorcentaje', method:'GET')
        "/api/bi/analisis/ventas/utilidad"(controller:'estadisticas', action: 'margenVenta', method:'GET')



        "/api/bi/analisisDeVentasMensuales"(resources: 'analisisDeVentaMensual')



        "/"(view:"/index")
        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}
