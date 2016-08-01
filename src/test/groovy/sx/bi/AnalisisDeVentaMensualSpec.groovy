package sx.bi


import grails.test.mixin.TestFor
import grails.validation.ValidationException
import spock.lang.Specification
import spock.lang.Unroll

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(AnalisisDeVentaMensual)
class AnalisisDeVentaMensualSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    @Unroll
    def "el codigo de error para el ejercicio: #year debe ser: #code "() {
        when: 'validamos'
        def m = new AnalisisDeVentaMensual(ejercicio: year, mes:Mes.ENE)
        m.validate()

        then: 'La validacion del ejercicio debe fallar'
        m.errors['ejercicio']?.code == code


        where:
        year | code
        2008 | 'range.toosmall'
        2010 | null
        2015 | null
        2022 | 'range.toobig'
        2023 | 'range.toobig'
        2024 | 'range.toobig'
    }



    def "salvar dos registros con mismo mes y ajercico debe generar un error de validacion"(){
        setup: 'Un registro de MargenDeVenta'
        def m = new AnalisisDeVentaMensual(ejercicio:2016, mes:Mes.ENE)
        m.save(flush:true)

        when:'Generamos una segunda entidad'
        def m2 = new AnalisisDeVentaMensual(ejercicio: 2016, mes:Mes.ENE)
        m2.save(failOnError:true)

        then:
        thrown(ValidationException)
        m2.hasErrors()
        println m2.errors['ejercicio'].code
        m2.errors['ejercicio']?.code == 'unique'
    }



    def 'it should have a valid constructor for ejercicio mes'(){
        expect: new AnalisisDeVentaMensual(2016, 1).mes == Mes.ENE
    }

    def 'debe poder buscar por ejercicio y mes '(){
        given: 'A valid instance'
        def m = new AnalisisDeVentaMensual(ejercicio: 2016, mes: Mes.ENE)

        when: 'persist the entity'
        m.save()

        then: 'should be found in the data store'
        def found = AnalisisDeVentaMensual.buscarPorEjercicioYMes(2016,1)
        found.mes == Mes.ENE
    }

    def 'it should persist a valid instance'(){
        given: 'A valid instance'
        def m = new AnalisisDeVentaMensual(ejercicio: 2016, mes:Mes.ENE)
        when: 'Persist the entity'
        m.save()

        then:
        m.id
        !m.hasErrors()
        AnalisisDeVentaMensual.get(m.id).mes == Mes.ENE
        println m
    }

}
