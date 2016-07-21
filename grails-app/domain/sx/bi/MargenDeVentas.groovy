package sx.bi


import grails.rest.*
import groovy.transform.ToString
import groovy.transform.EqualsAndHashCode


@ToString(includeNames=true,includePackage=false)
@EqualsAndHashCode(includes='ejercicio,mes')
@Resource(readOnly = false, formats = ['json'])
class MargenDeVentas {

    Integer ejercicio
    Mes mes
    BigDecimal importe = 0.0
    BigDecimal ytd = 0.0

    Date dateCreated
    Date lastUpdated

    public MargenDeVentas(){}

    public MargenDeVentas(int ejercicio, int mes){
        this.ejercicio = ejercicio
        this.mes = Mes.fromValue(mes)
    }

    static constraints = {
        ejercicio  range: 2010..2020 , unique: 'mes'
    }

    static mapping = {
        //id generator:'uuid'
        mes enumType: 'ordinal'
    }


    static buscarPorEjercicioYMes(Integer ejercicio, Integer mes){
        return MargenDeVentas.where {ejercicio==ejercicio && mes==Mes.fromValue(mes)}.find()
    }

}