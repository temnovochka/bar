package vinoteka.gui.controller

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.*
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import vinoteka.gui.Vinoteka


class RegisterViewController {

    @FXML lateinit var registerButton: Button
    @FXML lateinit var logInField: TextField
    @FXML lateinit var passwordField: PasswordField
    @FXML lateinit var nameField: TextField
    @FXML lateinit var documentField: TextField
    @FXML lateinit var birthdayField: DatePicker

    fun init() {}

    @FXML
    fun onRegisterButtonClicked(actionEvent: ActionEvent) {
        val login = logInField.text
        val password = passwordField.text
        val name = nameField.text
        val document = documentField.text
        val birthday = birthdayField.value

        if (login.isEmpty()) {
            val errorAlert = Alert(Alert.AlertType.ERROR)
            errorAlert.headerText = "Login is empty"
            errorAlert.contentText = "Please, input your login"
            errorAlert.showAndWait()
            return
        }

        if (password.isEmpty()) {
            val errorAlert = Alert(Alert.AlertType.ERROR)
            errorAlert.headerText = "Password is empty"
            errorAlert.contentText = "Please, input your password"
            errorAlert.showAndWait()
            return
        }

        if (name.isEmpty()) {
            val errorAlert = Alert(Alert.AlertType.ERROR)
            errorAlert.headerText = "Name is empty"
            errorAlert.contentText = "Please, input your name"
            errorAlert.showAndWait()
            return
        }

        if (document.isEmpty()) {
            val errorAlert = Alert(Alert.AlertType.ERROR)
            errorAlert.headerText = "Document data is empty"
            errorAlert.contentText = "Please, input your document data"
            errorAlert.showAndWait()
            return
        }

        if (birthday == null) {
            val errorAlert = Alert(Alert.AlertType.ERROR)
            errorAlert.headerText = "Birthday is empty"
            errorAlert.contentText = "Please, input your birthday"
            errorAlert.showAndWait()
            return
        }

        val birth = DateTime(DateTimeZone.UTC).withDate(
            birthday.year, birthday.monthValue, birthday.dayOfMonth
        ).withTime(0, 0, 0, 0)

        Vinoteka.bp.createClient(login, password, name, birth, document).getOr {
            val errorAlert = Alert(Alert.AlertType.ERROR)
            errorAlert.headerText = "Error"
            errorAlert.contentText = it
            errorAlert.showAndWait()
            return
        }

        Vinoteka.showVinotekaView()
    }


}