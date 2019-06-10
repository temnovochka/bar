package vinoteka.gui.controller

import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.*
import org.jetbrains.exposed.dao.with
import org.jetbrains.exposed.sql.transactions.transaction
import vinoteka.gui.Vinoteka
import vinoteka.model.*

class ManagerViewController {
    lateinit var manager: Manager

    @FXML
    lateinit var logoutButton: Button
    @FXML
    lateinit var backButton: Button
    @FXML
    lateinit var confirmNewClientsButton: Button
    @FXML
    lateinit var checkAllRegisteredOrdersButton: Button
    @FXML
    lateinit var registerOrderButton: Button
    @FXML
    lateinit var formNewPurchaseButton: Button
    @FXML
    lateinit var confirmOrderButton: Button
    @FXML
    lateinit var confirmClientButton: Button
    @FXML
    lateinit var addIntoPurchaseButton: Button
    @FXML
    lateinit var ordersList: ListView<Order>
    @FXML
    lateinit var clientsList: ListView<Client>
    @FXML
    lateinit var productsInStockList: ListView<Stock>
    @FXML
    lateinit var selectedProductsForPurchaseList: ListView<Stock>
    @FXML
    lateinit var confirmClientLabel: Label
    @FXML
    lateinit var confirmOrderLabel: Label
    @FXML
    lateinit var checkOrderLabel: Label
    @FXML
    lateinit var addIntoPurchaseLabel: Label

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
                    if (empty) text = null
                    else {
                        text =
                            "client_id = ${item?.id}, birthday = ${item?.birthday?.toDate()}, document = ${item?.document}"
                    }
                }
            }
        }

        clientsList.items.addAll(Vinoteka.bp.getClients().getOr {
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
                    if (empty) text = null
                    else {
                        text = "order_id = ${item?.id}, status = ${item?.status}"
                    }
                }
            }
        }

        ordersList.items.addAll(Vinoteka.bp.getManagerOrders().getOr {
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
        backButton.isVisible = true
        confirmNewClientsButton.isVisible = false
        checkAllRegisteredOrdersButton.isVisible = false
        registerOrderButton.isVisible = false
        formNewPurchaseButton.isVisible = false
        productsInStockList.isVisible = true
        selectedProductsForPurchaseList.isVisible = true
        addIntoPurchaseButton.isVisible = true

        val list = FXCollections.observableArrayList<Stock>()
        productsInStockList.items = list
        productsInStockList.setCellFactory {
            object : ListCell<Stock>() {
                override fun updateItem(item: Stock?, empty: Boolean) {
                    super.updateItem(item, empty)
                    if (empty) text = null
                    else {
                        transaction {
                            text = "product = ${item?.product?.name}, number = ${item?.number}"
                        }
                    }
                }
            }
        }

        val listForSelected = FXCollections.observableArrayList<Stock>()
        selectedProductsForPurchaseList.items = listForSelected

        selectedProductsForPurchaseList.setCellFactory {
            object : ListCell<Stock>() {
                override fun updateItem(item: Stock?, empty: Boolean) {
                    super.updateItem(item, empty)
                    if (empty) text = null
                    transaction {
                        text = "product = ${item?.product?.name}"
                    }
                }
            }
        }

        transaction {
            list.addAll(Stock.all())
        }

        productsInStockList.selectionModel.selectionMode = SelectionMode.MULTIPLE

        productsInStockList.setOnMouseClicked {
            listForSelected.add(productsInStockList.selectionModel.selectedItems.first())
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
        Vinoteka.bp.confirmClient(client).getOr {
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
        Vinoteka.bp.confirmOrder(order, manager).getOr {
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
        backButton.isVisible = true
        confirmNewClientsButton.isVisible = false
        checkAllRegisteredOrdersButton.isVisible = false
        registerOrderButton.isVisible = false
        formNewPurchaseButton.isVisible = false
        ordersList.isVisible = false
        checkOrderLabel.isVisible = true

        Vinoteka.bp.checkOrders(manager).getOr {
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
        addIntoPurchaseLabel.isVisible = true

        val intoPurchase = selectedProductsForPurchaseList.items.groupBy { it }.mapValues { (_, v) -> v.size }
        Vinoteka.bp.makeManagerPurchase(intoPurchase, manager).getOr {
            val errorAlert = Alert(Alert.AlertType.ERROR)
            errorAlert.headerText = "Error"
            errorAlert.contentText = it
            errorAlert.showAndWait()
            init(manager)
            return
        }
    }

}