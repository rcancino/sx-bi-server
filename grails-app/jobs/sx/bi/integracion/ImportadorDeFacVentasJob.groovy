package sx.bi.integracion


class ImportadorDeFacVentasJob {

    def group = 'Importadores'
    def description = 'Importador de FacVentasDet'

    static triggers = {
        cron name: 'Trigger diario', startDelay: 60000, cronExpression:"0 0 20 ? * *"
        //simple repeatInterval: 5000l // execute job once in 5 seconds
    }

    def execute() {
        log.info('Importando FacVentasDet....')

    }




}
