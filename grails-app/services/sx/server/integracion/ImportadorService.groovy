package sx.server.integracion

import grails.transaction.Transactional
import groovy.sql.Sql
import org.apache.commons.lang.exception.ExceptionUtils
import sx.tesoreria.*


class ImportadorService {

	def dataSource_importacion


    def persist(domain){
    	try {
    		domain.save failOnError:true,flush:true
    	}
    	catch(Exception e) {
    		println ExceptionUtils.getRootCause(e)

    		log.error e,"Error importando  Error: "+ExceptionUtils.getRootCauseMessage(e)		
    	}
    }

    private Sql buildSql(){
    	return new Sql(dataSource_importacion)
    }


}
