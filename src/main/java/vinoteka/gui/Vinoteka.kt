package vinoteka.gui

import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.layout.AnchorPane
import javafx.stage.Stage
import vinoteka.bp.BPFacade
import vinoteka.bp.BPFacadeImpl
import vinoteka.gui.controller.AdminViewController
import vinoteka.gui.controller.ClientViewController
import vinoteka.gui.controller.ManagerViewController
import vinoteka.gui.controller.RegisterViewController
import vinoteka.model.Admin
import vinoteka.model.Client
import vinoteka.model.Manager


object Vinoteka {

    val bp: BPFacade = BPFacadeImpl

    lateinit var myStage: Stage
    fun start(primaryStage: Stage) {
        myStage = primaryStage
        showVinotekaView()
        myStage.show()
    }

    fun showVinotekaView() {
        val fxmlFile = "/VinotekaView.fxml"
        val loader = FXMLLoader()
        val root = loader.load<AnchorPane>(Vinoteka::class.java.getResourceAsStream(fxmlFile))
        val scene = Scene(root)
        myStage.scene = scene
    }

    fun showClientView(client: Client) {
        val fxmlFile = "/ClientView.fxml"
        val loader = FXMLLoader()
        val root = loader.load<AnchorPane>(Vinoteka::class.java.getResourceAsStream(fxmlFile))
        val dvc = loader.getController<ClientViewController>()
        dvc.init(client)
        val scene = Scene(root)
        myStage.scene = scene
    }

    fun showAdminView(admin: Admin) {
        val fxmlFile = "/AdminView.fxml"
        val loader = FXMLLoader()
        val root = loader.load<AnchorPane>(Vinoteka::class.java.getResourceAsStream(fxmlFile))
        val dvc = loader.getController<AdminViewController>()
        dvc.init(admin)
        val scene = Scene(root)
        myStage.scene = scene
    }

    fun showManagerView(manager: Manager) {
        val fxmlFile = "/ManagerView.fxml"
        val loader = FXMLLoader()
        val root = loader.load<AnchorPane>(Vinoteka::class.java.getResourceAsStream(fxmlFile))
        val dvc = loader.getController<ManagerViewController>()
        dvc.init(manager)
        val scene = Scene(root)
        myStage.scene = scene
    }

    fun showRegisterView() {
        val fxmlFile = "/RegisterView.fxml"
        val loader = FXMLLoader()
        val root = loader.load<AnchorPane>(Vinoteka::class.java.getResourceAsStream(fxmlFile))
        val dvc = loader.getController<RegisterViewController>()
        dvc.init()
        val scene = Scene(root)
        myStage.scene = scene
    }
}