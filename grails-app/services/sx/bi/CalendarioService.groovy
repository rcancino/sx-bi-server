package sx.bi

import grails.transaction.Transactional

import org.apache.commons.lang.exception.ExceptionUtils;

import sx.Periodo

import grails.transaction.Transactional
import java.math.*

@Transactional
class CalendarioService {


		def generarPeriodosSemanales(int year){
			def periodo=Periodo.getPeriodoAnual(year)

				def folio=1
				def mes=1
				def semanas=[]
				def inicioDeSemana
				 for(Date dia:periodo.fechaInicial..periodo.fechaFinal){
				
					if(inicioDeSemana==null){
				 		 inicioDeSemana=dia
					}
					if(dia.day==0){
				  		def semana=new Periodo(inicioDeSemana,dia)
				  		semanas<<semana
				 		inicioDeSemana=null
				  		//println 'Semana: '+semana
					}	

					if(dia==periodo.fechaFinal){
						println "Creando la ultima semana del ejercicio"
						def semana=new Periodo(inicioDeSemana,dia)
				  		semanas<<semana
				 		inicioDeSemana=null
					}
			    }

			for(int i=0;i<semanas.size();i++){
				
				def per=semanas[i]

				def mesInicial=Periodo.obtenerMes(semanas[i].fechaInicial)+1
				
				def mesFinal=Periodo.obtenerMes(semanas[i].fechaFinal)+1

				mes=mesInicial

				def bimestre

				def  trimestre

				def cuatrimestre

				def semestre

				if(mesInicial!=mesFinal){
					println "Los meses NO son iguales"

					def periodoMesInicial=Periodo.getPeriodoEnUnMes(mes)

					if(periodoMesInicial.fechaFinal.day<=3){
						mes=mesFinal
					}

				}else{
					println "Los meses son iguales"
				}

				bimestre=(mes/2).setScale(0,RoundingMode.UP).intValue()

				trimestre=(mes/3).setScale(0,RoundingMode.UP).intValue()

				cuatrimestre=(mes/4).setScale(0,RoundingMode.UP).intValue()

				semestre=(mes/6).setScale(0,RoundingMode.UP).intValue()

				println "Semana: $i  "+ semanas[i].fechaInicial +"   "+semanas[i].fechaFinal+"  mes: "+mes + "  bimestre: "+ bimestre+
						" trimestre: "+trimestre +" cuatrimestre: "+ cuatrimestre +" semestre: "+semestre

					def calendario=new CalendarioBi(
						semana:i+1,
						fechaInicial:semanas[i].fechaInicial,
						fechaFinal:semanas[i].fechaFinal,
						mes:mes,
						bimestre:bimestre,
						trimestre:trimestre,
						cuatrimestre:cuatrimestre,
						semestre:semestre,
						year:year
					)

					calendario.save failOnError:true
				
	  		}


		}
}
	
	




