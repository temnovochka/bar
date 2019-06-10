package vinoteka.gui.controller

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Button
import vinoteka.gui.Vinoteka
import vinoteka.model.Admin

class AdminViewController {
    lateinit var admin: Admin

    @FXML
    lateinit var logoutButton: Button
    @FXML
    lateinit var backButton: Button
    @FXML
    lateinit var checkPurchasesButton: Button
    @FXML
    lateinit var formPurchaseButton: Button
    @FXML
    lateinit var getNewWineButton: Button

    fun init(admin: Admin) {
        this.admin = admin
        logoutButton.isVisible = true
        backButton.isVisible = false
        checkPurchasesButton.isVisible = true
        formPurchaseButton.isVisible = true
        getNewWineButton.isVisible = true
    }

    @FXML
    fun onLogoutButtonClicked(actionEvent: ActionEvent) {
        Vinoteka.showVinotekaView()
    }

    @FXML
    fun onBackButtonClicked(actionEvent: ActionEvent) {
        init(admin)
    }

    @FXML
    fun onCheckPurchasesButtonClicked(actionEvent: ActionEvent) {
        backButton.isVisible = true
        checkPurchasesButton.isVisible = false
        formPurchaseButton.isVisible = false
        getNewWineButton.isVisible = false

    }

    @FXML
    fun onFormPurchaseButtonClicked(actionEvent: ActionEvent) {
        backButton.isVisible = true
        checkPurchasesButton.isVisible = false
        formPurchaseButton.isVisible = false
        getNewWineButton.isVisible = false

    }

    @FXML
    fun onGetNewWineButtonClicked(actionEvent: ActionEvent) {
        backButton.isVisible = true
        checkPurchasesButton.isVisible = false
        formPurchaseButton.isVisible = false
        getNewWineButton.isVisible = false

    }


}