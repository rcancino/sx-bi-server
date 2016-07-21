package sx.bi

import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.*

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestMixin(GrailsUnitTestMixin)
class MesSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "debe de crear instancias con enteros validos "() {
        expect:
        Mes.fromValue(1) == Mes.ENE
        Mes.fromValue(6) == Mes.JUN
        Mes.fromValue(11) == Mes.NOV
    }

    void "it should throw ane exception when creating from an invalid number"(){
        when: 'An invalid number is used'
        Mes.fromValue(0)
        then: 'En exception should be thrown'
        thrown(IllegalArgumentException)
    }

    def "debe ordenar en base al id"(){
        setup: 'una coleccion de meses'
        def meses = [Mes.FEB,Mes.DIC,Mes.MAR, Mes.ENE]

        when: 'cuando ordenamos'
        def res = meses.sort()

        then: ''
        res.get(0) == Mes.ENE
        res.get(1) == Mes.FEB
        res.get(2) == Mes.MAR
        res.get(3) == Mes.DIC
    }
}