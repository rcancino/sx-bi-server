package sx.bi


import grails.rest.*
import groovy.transform.ToString
import groovy.transform.EqualsAndHashCode

import java.math.MathContext
import java.math.RoundingMode


@ToString(includeNames=true,includePackage=false)
@EqualsAndHashCode(includes='ejercicio,mes')
class AnalisisDeVentaMensual {

    Integer ejercicio
    Mes mes

    BigDecimal ventaNeta = 0.0
    BigDecimal costoNeto = 0.0
    BigDecimal ventaNetaYtd = 0.0
    BigDecimal costoNetoYtd = 0.0
    BigDecimal utilidad = 0.0
    BigDecimal utilidadYtd = 0.0
    BigDecimal margen = 0.0
    BigDecimal margenYtd = 0.0

    Date dateCreated
    Date lastUpdated

    public AnalisisDeVentaMensual(){}

    public AnalisisDeVentaMensual(int ejercicio, int mes){
        this.ejercicio = ejercicio
        this.mes = Mes.fromValue(mes)
    }

    static constraints = {
        ejercicio  range: 2010..2020 , unique: 'mes'
    }

    static mapping = {
        //id generator:'uuid'
        mes enumType: 'ordinal'
        utilidad formula: 'venta_neta - costo_neto'
        utilidadYtd formula: 'venta_neta_ytd - costo_neto_ytd'

    }

    static transients = ['margen']


    BigDecimal getMargen(){
        if(costoNeto){
            return  ((ventaNeta-costoNeto) * 100.00 / costoNeto).setScale(2,RoundingMode.HALF_EVEN)
        }
        return 0.0
    }

    BigDecimal getMargenYtd(){
        if(costoNetoYtd){
            return  ((utilidadYtd) * 100.00 / costoNetoYtd).setScale(2,RoundingMode.HALF_EVEN)
        }
        return 0.0
    }

    static buscarPorEjercicioYMes(Integer ejercicio, Integer mes){
        return AnalisisDeVentaMensual.where {ejercicio==ejercicio && mes==Mes.fromValue(mes)}.find()
    }


}