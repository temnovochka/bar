package vinoteka.gui

import javafx.application.Application
import javafx.stage.Stage
import vinoteka.db.Db
import vinoteka.service.VineData
import vinoteka.service.VinoService

class Main : Application() {
    override fun start(primaryStage: Stage) {
        Db.init()
        VineData.actualizeProducts()
        VinoService.start()
        Vinoteka.start(primaryStage)
    }

    override fun stop() {
        super.stop()
        VinoService.stop()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(Main::class.java)
        }
    }

}
