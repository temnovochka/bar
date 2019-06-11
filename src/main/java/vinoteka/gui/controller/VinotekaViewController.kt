package vinoteka.gui.controller

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.Button
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField
import vinoteka.gui.Vinoteka
import vinoteka.model.Admin
import vinoteka.model.Client
import vinoteka.model.Manager


class VinotekaViewController {

    @FXML lateinit var logInButton: Button
    @FXML lateinit var registerButton: Button
    @FXML lateinit var logInField: TextField
    @FXML lateinit var passwordField: PasswordField

    @FXML
    fun onLogInButtonClicked(actionEvent: ActionEvent) {
        val login = logInField.text
        val password = passwordField.text

        if (login.isEmpty()) {
            val errorAlert = Alert(AlertType.ERROR)
            errorAlert.headerText = "Login is empty"
            errorAlert.contentText = "Please, input your login"
            errorAlert.showAndWait()
            return
        }

        if (password.isEmpty()) {
            val errorAlert = Alert(AlertType.ERROR)
            errorAlert.headerText = "Password is empty"
            errorAlert.contentText = "Please, input your password"
            errorAlert.showAndWait()
            return
        }

        val user = Vinoteka.bp.getUser(logInField.text, passwordField.text).getOr {
            val errorAlert = Alert(AlertType.ERROR)
            errorAlert.headerText = "Error"
            errorAlert.contentText = it
            errorAlert.showAndWait()
            return
        }

        when (user) {
            is Client -> Vinoteka.showClientView(user)
            is Manager -> Vinoteka.showManagerView(user)
            is Admin -> Vinoteka.showAdminView(user)
        }
    }

    @FXML
    fun onRegisterButtonClicked(actionEvent: ActionEvent) {
        Vinoteka.showRegisterView()
    }

}