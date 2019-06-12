package vinoteka.gui.controller

import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.*
import vinoteka.gui.Vinoteka
import vinoteka.model.Client
import vinoteka.model.Manager
import vinoteka.model.Order
import vinoteka.model.OrderStatus

class ManagerViewController {
    lateinit var manager: Manager

    @FXML lateinit var logoutButton: Button
    @FXML lateinit var backButton: Button
    @FXML lateinit var confirmNewClientsButton: Button
    @FXML lateinit var checkAllRegisteredOrdersButton: Button
    @FXML lateinit var registerOrderButton: Button
    @FXML lateinit var formNewPurchaseButton: Button
    @FXML lateinit var confirmOrderButton: Button
    @FXML lateinit var confirmClientButton: Button
    @FXML lateinit var addIntoPurchaseButton: Button
    @FXML lateinit var putPurchasesIntoStockButton: Button

    @FXML lateinit var ordersList: ListView<Order>
    @FXML lateinit var clientsList: ListView<Client>
    @FXML lateinit var productsInStockList: ListView<Pair<String, Int>>
    @FXML lateinit var selectedProductsForPurchaseList: ListView<String>

    @FXML lateinit var confirmClientLabel: Label
    @FXML lateinit var confirmOrderLabel: Label
    @FXML lateinit var checkOrderLabel: Label
    @FXML lateinit var addIntoPurchaseLabel: Label
    @FXML lateinit var putPurchasesIntoStockLabel: Label

    fun init(manager: Manager) {
        this.manager = manager
        logoutButton.isVisible = true
        backButton.isVisible = false
        confirmNewClientsButton.isVisible = true
        checkAllRegisteredOrdersButton.isVisible = true
        registerOrderButton.isVisible = true
        formNewPurchaseButton.isVisible = true
        confirmClientButton.isVisible = false
        confirmOrderButton.isVisible = false
        ordersList.isVisible = false
        clientsList.isVisible = false
        confirmClientLabel.isVisible = false
        confirmOrderLabel.isVisible = false
        checkOrderLabel.isVisible = false
        productsInStockList.isVisible = false
        addIntoPurchaseButton.isVisible = false
        selectedProductsForPurchaseList.isVisible = false
        addIntoPurchaseLabel.isVisible = false
        putPurchasesIntoStockButton.isVisible = true
        putPurchasesIntoStockLabel.isVisible = false
    }

    @FXML
    fun onLogoutButtonClicked(actionEvent: ActionEvent) {
        Vinoteka.showVinotekaView()
    }

    @FXML
    fun onBackButtonClicked(actionEvent: ActionEvent) {
        init(manager)
    }

    @FXML
    fun onConfirmNewClientsButtonClicked(actionEvent: ActionEvent) {
        putPurchasesIntoStockButton.isVisible = false
        backButton.isVisible = true
        confirmNewClientsButton.isVisible = false
        checkAllRegisteredOrdersButton.isVisible = false
        registerOrderButton.isVisible = false
        formNewPurchaseButton.isVisible = false
        clientsList.isVisible = true

        val list = FXCollections.observableArrayList<Client>()
        clientsList.items = list

        clientsList.setCellFactory {
            object : ListCell<Client>() {
                override fun updateItem(item: Client?, empty: Boolean) {
                    super.updateItem(item, empty)
                    text = when {
                        empty -> null
                        else -> "client_id = ${item?.id}, birthday = ${item?.birthday?.toDate()}, document = ${item?.document}"
                    }
                }
            }
        }

        clientsList.items.addAll(Manager.getClients().getOr {
            val errorAlert = Alert(Alert.AlertType.ERROR)
            errorAlert.headerText = "Error"
            errorAlert.contentText = it
            errorAlert.showAndWait()
            init(manager)
            return
        })

        clientsList.setOnMouseClicked {
            val client = clientsList.selectionModel.selectedItems.first()
            confirmClientButton.isVisible = client.isConfirmed == false
        }
    }

    @FXML
    fun onRegisterOrderButtonClicked(actionEvent: ActionEvent) {
        putPurchasesIntoStockButton.isVisible = false
        backButton.isVisible = true
        confirmNewClientsButton.isVisible = false
        checkAllRegisteredOrdersButton.isVisible = false
        registerOrderButton.isVisible = false
        formNewPurchaseButton.isVisible = false
        ordersList.isVisible = true

        val list = FXCollections.observableArrayList<Order>()
        ordersList.items = list

        ordersList.setCellFactory {
            object : ListCell<Order>() {
                override fun updateItem(item: Order?, empty: Boolean) {
                    super.updateItem(item, empty)
                    text = when {
                        empty -> null
                        else -> "order_id = ${item?.id}, status = ${item?.status}"
                    }
                }
            }
        }

        ordersList.items.addAll(Manager.getManagerOrders().getOr {
            val errorAlert = Alert(Alert.AlertType.ERROR)
            errorAlert.headerText = "Error"
            errorAlert.contentText = it
            errorAlert.showAndWait()
            init(manager)
            return
        })

        ordersList.setOnMouseClicked {
            val order = ordersList.selectionModel.selectedItems.first()
            confirmOrderButton.isVisible = order.status == OrderStatus.NEW
        }
    }

    @FXML
    fun onFormNewPurchaseButtonClicked(actionEvent: ActionEvent) {
        putPurchasesIntoStockButton.isVisible = false
        backButton.isVisible = true
        confirmNewClientsButton.isVisible = false
        checkAllRegisteredOrdersButton.isVisible = false
        registerOrderButton.isVisible = false
        formNewPurchaseButton.isVisible = false
        productsInStockList.isVisible = true
        selectedProductsForPurchaseList.isVisible = true
        addIntoPurchaseButton.isVisible = true

        val list = FXCollections.observableArrayList<Pair<String, Int>>()
        productsInStockList.items = list
        productsInStockList.setCellFactory {
            object : ListCell<Pair<String, Int>>() {
                override fun updateItem(item: Pair<String, Int>?, empty: Boolean) {
                    super.updateItem(item, empty)
                    text = when {
                        empty -> null
                        else -> "product = ${item?.first}, number = ${item?.second}"
                    }
                }
            }
        }

        val listForSelected = FXCollections.observableArrayList<String>()
        selectedProductsForPurchaseList.items = listForSelected

        selectedProductsForPurchaseList.setCellFactory {
            object : ListCell<String>() {
                override fun updateItem(item: String?, empty: Boolean) {
                    super.updateItem(item, empty)
                    text = when {
                        empty -> null
                        else -> "product = $item"
                    }
                }
            }
        }

        list.addAll(Manager.getAllFromStock().getOr {
            val errorAlert = Alert(Alert.AlertType.ERROR)
            errorAlert.headerText = "Error"
            errorAlert.contentText = it
            errorAlert.showAndWait()
            init(manager)
            return
        })

        productsInStockList.selectionModel.selectionMode = SelectionMode.MULTIPLE

        productsInStockList.setOnMouseClicked {
            listForSelected.add(productsInStockList.selectionModel.selectedItems.first().first)
        }
    }

    @FXML
    fun onConfirmClientButtonClicked(actionEvent: ActionEvent) {
        backButton.isVisible = true
        confirmNewClientsButton.isVisible = false
        checkAllRegisteredOrdersButton.isVisible = false
        registerOrderButton.isVisible = false
        formNewPurchaseButton.isVisible = false
        clientsList.isVisible = false
        confirmClientLabel.isVisible = true

        val client = clientsList.selectionModel.selectedItems.first()
        Manager.confirmClient(client).getOr {
            val errorAlert = Alert(Alert.AlertType.ERROR)
            errorAlert.headerText = "Error"
            errorAlert.contentText = it
            errorAlert.showAndWait()
            init(manager)
            return
        }
    }

    @FXML
    fun onConfirmOrderButtonClicked(actionEvent: ActionEvent) {
        backButton.isVisible = true
        confirmNewClientsButton.isVisible = false
        checkAllRegisteredOrdersButton.isVisible = false
        registerOrderButton.isVisible = false
        formNewPurchaseButton.isVisible = false
        ordersList.isVisible = false
        confirmOrderLabel.isVisible = true

        val order = ordersList.selectionModel.selectedItems.first()
        Manager.confirmOrder(order, manager).getOr {
            val errorAlert = Alert(Alert.AlertType.ERROR)
            errorAlert.headerText = "Error"
            errorAlert.contentText = it
            errorAlert.showAndWait()
            init(manager)
            return
        }
    }

    @FXML
    fun onCheckAllRegisteredOrdersButtonClicked(actionEvent: ActionEvent) {
        putPurchasesIntoStockButton.isVisible = false
        backButton.isVisible = true
        confirmNewClientsButton.isVisible = false
        checkAllRegisteredOrdersButton.isVisible = false
        registerOrderButton.isVisible = false
        formNewPurchaseButton.isVisible = false
        ordersList.isVisible = false
        checkOrderLabel.isVisible = true

        Manager.checkOrders(manager).getOr {
            val errorAlert = Alert(Alert.AlertType.ERROR)
            errorAlert.headerText = "Error"
            errorAlert.contentText = it
            errorAlert.showAndWait()
            init(manager)
            return
        }
    }

    @FXML
    fun onAddIntoPurchaseButtonButtonClicked(actionEvent: ActionEvent) {
        backButton.isVisible = true
        confirmNewClientsButton.isVisible = false
        checkAllRegisteredOrdersButton.isVisible = false
        registerOrderButton.isVisible = false
        formNewPurchaseButton.isVisible = false
        ordersList.isVisible = false
        addIntoPurchaseButton.isVisible = false
        selectedProductsForPurchaseList.isVisible = false
        productsInStockList.isVisible = false
        addIntoPurchaseLabel.isVisible = true

        val intoPurchase = selectedProductsForPurchaseList.items.groupBy { it }.mapValues { (_, v) -> v.size }
        Manager.makeManagerPurchase(intoPurchase, manager).getOr {
            val errorAlert = Alert(Alert.AlertType.ERROR)
            errorAlert.headerText = "Error"
            errorAlert.contentText = it
            errorAlert.showAndWait()
            init(manager)
            return
        }
    }

    fun onPutPurchasesIntoStockButtonClicked(actionEvent: ActionEvent) {
        putPurchasesIntoStockButton.isVisible = false
        backButton.isVisible = true
        confirmNewClientsButton.isVisible = false
        checkAllRegisteredOrdersButton.isVisible = false
        registerOrderButton.isVisible = false
        formNewPurchaseButton.isVisible = false
        ordersList.isVisible = false
        putPurchasesIntoStockLabel.isVisible = true

        Manager.putDonePurchasesIntoStock(manager).getOr {
            val errorAlert = Alert(Alert.AlertType.ERROR)
            errorAlert.headerText = "Error"
            errorAlert.contentText = it
            errorAlert.showAndWait()
            init(manager)
            return
        }
    }

}