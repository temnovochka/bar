package vinoteka.gui

import javafx.application.Application
import javafx.stage.Stage
import vinoteka.db.Db

class Main : Application() {
    override fun start(primaryStage: Stage) {
        Db.init()
        Vinoteka.start(primaryStage)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(Main::class.java)
        }
    }

}
