package vinoteka.gui.controller

import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.*
import vinoteka.gui.Vinoteka
import vinoteka.model.Admin
import vinoteka.model.OrderStatus
import vinoteka.model.Purchase


class AdminViewController {
    lateinit var admin: Admin

    @FXML lateinit var logoutButton: Button
    @FXML lateinit var mainMenuButton: Button
    @FXML lateinit var checkPurchasesButton: Button
    @FXML lateinit var editPurchaseButton: Button
    @FXML lateinit var getNewWineButton: Button
    @FXML lateinit var saveChangesPurchaseButton: Button
    @FXML lateinit var makeDonePurchaseButton: Button

    @FXML lateinit var allPurchasesList: ListView<Purchase>
    @FXML lateinit var detailOfPurchaseList: ListView<String>

    @FXML lateinit var supplierField: TextField

    @FXML lateinit var savedChangesLabel: Label
    @FXML lateinit var purchaseMadeDoneLabel: Label

    fun init(admin: Admin) {
        this.admin = admin
        logoutButton.isVisible = true
        mainMenuButton.isVisible = false
        checkPurchasesButton.isVisible = true
        editPurchaseButton.isVisible = false
        getNewWineButton.isVisible = true
        allPurchasesList.isVisible = false
        detailOfPurchaseList.isVisible = false
        saveChangesPurchaseButton.isVisible = false
        supplierField.isVisible = false
        savedChangesLabel.isVisible = false
        makeDonePurchaseButton.isVisible = false
        purchaseMadeDoneLabel.isVisible = false
    }

    @FXML
    fun onLogoutButtonClicked(actionEvent: ActionEvent) {
        Vinoteka.showVinotekaView()
    }

    @FXML
    fun onMainMenuButtonClicked(actionEvent: ActionEvent) {
        init(admin)
    }

    @FXML
    fun onLookPurchasesButtonClicked(actionEvent: ActionEvent) {
        mainMenuButton.isVisible = true
        checkPurchasesButton.isVisible = false
        editPurchaseButton.isVisible = false
        getNewWineButton.isVisible = false
        allPurchasesList.isVisible = true
        editPurchaseButton.isVisible = false

        val list = FXCollections.observableArrayList<Purchase>()
        allPurchasesList.items = list

        allPurchasesList.setCellFactory {
            object : ListCell<Purchase>() {
                override fun updateItem(item: Purchase?, empty: Boolean) {
                    super.updateItem(item, empty)
                    text = when {
                        empty -> null
                        else -> "purchase_id = ${item?.id}, status = ${item?.status}, formed = ${item?.formedDate?.toDate()}"
                    }
                }
            }
        }

        allPurchasesList.items.addAll(Admin.getAllPurchases().getOr {
            val errorAlert = Alert(Alert.AlertType.ERROR)
            errorAlert.headerText = "Error"
            errorAlert.contentText = it
            errorAlert.showAndWait()
            init(admin)
            return
        })

        allPurchasesList.setOnMouseClicked {
            val purchase = allPurchasesList.selectionModel.selectedItems.first()
            editPurchaseButton.isVisible = purchase.status in setOf(OrderStatus.IN_PROGRESS, OrderStatus.NEW)
        }
    }

    @FXML
    fun onEditPurchaseButtonClicked(actionEvent: ActionEvent) {
        mainMenuButton.isVisible = true
        checkPurchasesButton.isVisible = false
        editPurchaseButton.isVisible = false
        getNewWineButton.isVisible = false
        allPurchasesList.isVisible = false

        detailOfPurchaseList.isVisible = true
        saveChangesPurchaseButton.isVisible = true
        supplierField.isVisible = true

        val purchase = allPurchasesList.selectionModel.selectedItems.first()

        supplierField.text = purchase.supplier

        val list = FXCollections.observableArrayList<String>()
        detailOfPurchaseList.items = list

        detailOfPurchaseList.setCellFactory {
            object : ListCell<String>() {
                override fun updateItem(item: String?, empty: Boolean) {
                    super.updateItem(item, empty)
                    text = when {
                        empty -> null
                        else -> item
                    }
                }
            }
        }

        detailOfPurchaseList.items.addAll(Admin.getDetailInfoOfPurchase(purchase).getOr {
            val errorAlert = Alert(Alert.AlertType.ERROR)
            errorAlert.headerText = "Error"
            errorAlert.contentText = it
            errorAlert.showAndWait()
            init(admin)
            return
        })
    }

    @FXML
    fun onGetNewWineButtonClicked(actionEvent: ActionEvent) {
        mainMenuButton.isVisible = true
        checkPurchasesButton.isVisible = false
        editPurchaseButton.isVisible = false
        getNewWineButton.isVisible = false
        makeDonePurchaseButton.isVisible = false
        allPurchasesList.isVisible = true

        val list = FXCollections.observableArrayList<Purchase>()
        allPurchasesList.items = list

        allPurchasesList.setCellFactory {
            object : ListCell<Purchase>() {
                override fun updateItem(item: Purchase?, empty: Boolean) {
                    super.updateItem(item, empty)
                    text = when {
                        empty -> null
                        else -> "purchase_id = ${item?.id}, status = ${item?.status}, formed = ${item?.formedDate?.toDate()}"
                    }
                }
            }
        }

        allPurchasesList.items.addAll(Admin.getAllPurchases().getOr {
            val errorAlert = Alert(Alert.AlertType.ERROR)
            errorAlert.headerText = "Error"
            errorAlert.contentText = it
            errorAlert.showAndWait()
            init(admin)
            return
        })

        allPurchasesList.setOnMouseClicked {
            val purchase = allPurchasesList.selectionModel.selectedItems.first()
            makeDonePurchaseButton.isVisible = purchase.status == OrderStatus.IN_PROGRESS
        }
    }

    fun onSaveChangesPurchaseButtonClicked(actionEvent: ActionEvent) {
        savedChangesLabel.isVisible = true

        detailOfPurchaseList.isVisible = false
        saveChangesPurchaseButton.isVisible = false
        supplierField.isVisible = false

        val purchase = allPurchasesList.selectionModel.selectedItems.first()
        val supplier = supplierField.text

        Admin.saveChangesInPurchase(admin, purchase, supplier).getOr {
            val errorAlert = Alert(Alert.AlertType.ERROR)
            errorAlert.headerText = "Error"
            errorAlert.contentText = it
            errorAlert.showAndWait()
            return
        }
    }

    fun onMakeDonePurchaseButtonClicked(actionEvent: ActionEvent) {
        mainMenuButton.isVisible = true
        makeDonePurchaseButton.isVisible = false
        allPurchasesList.isVisible = false
        purchaseMadeDoneLabel.isVisible = true

        val purchase = allPurchasesList.selectionModel.selectedItems.first()

        Admin.makePurchaseDone(admin, purchase).getOr {
            val errorAlert = Alert(Alert.AlertType.ERROR)
            errorAlert.headerText = "Error"
            errorAlert.contentText = it
            errorAlert.showAndWait()
            return
        }
    }


}