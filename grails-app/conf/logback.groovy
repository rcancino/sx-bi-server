import grails.util.BuildSettings
import grails.util.Environment

// See http://logback.qos.ch/manual/groovy.html for details on configuration
appender('STDOUT', ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%level %logger - %msg%n"
    }
}

root(ERROR, ['STDOUT'])

def targetDir = BuildSettings.TARGET_DIR
if (Environment.isDevelopmentMode() && targetDir) {
    appender("FULL_STACKTRACE", FileAppender) {
        file = "${targetDir}/stacktrace.log"
        append = true
        encoder(PatternLayoutEncoder) {
            pattern = "%level %logger - %msg%n"
        }
    }
    logger("StackTrace", ERROR, ['FULL_STACKTRACE'], false)
    
}
if(Environment.current == Environment.DEVELOPMENT){
    logger("grails.app.services.sx.server.integracion",INFO,['STDOUT'],false)
    logger("grails.services.sx.bi.kpi",INFO,['STDOUT'],false)
    logger("grails.app.services.sx.bi",INFO,['STDOUT'],false)
    logger("grails.app.services.sx.bi",INFO,['STDOUT'],false)
    logger("grails.app.controllers",INFO,['STDOUT'],false)
    logger("grails.app.jobs",INFO,['STDOUT'],false)
}
