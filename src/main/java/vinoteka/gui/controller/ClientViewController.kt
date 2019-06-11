package vinoteka.gui.controller

import javafx.event.ActionEvent
import javafx.fxml.FXML
import vinoteka.gui.Vinoteka
import javafx.collections.FXCollections
import javafx.scene.control.*
import org.jetbrains.exposed.sql.transactions.transaction
import vinoteka.model.*


class ClientViewController {
    lateinit var client: Client

    @FXML lateinit var existedOrdersButton: Button
    @FXML lateinit var newOrderButton: Button
    @FXML lateinit var logoutButton: Button
    @FXML lateinit var backButton: Button
    @FXML lateinit var productList: ListView<Product>
    @FXML lateinit var messageLabel: Label
    @FXML lateinit var existedOrdersList: ListView<Order>
    @FXML lateinit var confirmOrderButton: Button
    @FXML lateinit var selectedProductsForOrderList: ListView<Product>
    @FXML lateinit var payButton: Button
    @FXML lateinit var messageConfirmedOrderLabel: Label
    @FXML lateinit var messagePaidOrderLabel: Label

    fun init(client: Client) {
        this.client = client
        existedOrdersButton.isVisible = true
        newOrderButton.isVisible = true
        productList.isVisible = false
        existedOrdersList.isVisible = false
        messageLabel.isVisible = false
        backButton.isVisible = false
        confirmOrderButton.isVisible = false
        selectedProductsForOrderList.isVisible = false
        payButton.isVisible = false
        messageConfirmedOrderLabel.isVisible = false
        messagePaidOrderLabel.isVisible = false

        if (!client.isConfirmed) {
            newOrderButton.isDisable = true
            existedOrdersButton.isDisable = true
            messageLabel.isVisible = true
        }
    }

    @FXML
    fun onExistedOrdersButtonClicked(actionEvent: ActionEvent) {
        existedOrdersButton.isVisible = false
        newOrderButton.isVisible = false
        existedOrdersList.isVisible = true
        backButton.isVisible = true

        val list = FXCollections.observableArrayList<Order>()
        existedOrdersList.items = list

        existedOrdersList.setCellFactory {
            object : ListCell<Order>() {
                override fun updateItem(item: Order?, empty: Boolean) {
                    super.updateItem(item, empty)
                    if (empty) text = null
                    else {
                        text = "order_id = ${item?.id}, status = ${item?.status}, paid = ${item?.paymentStatus}"
                    }
                }
            }
        }

        existedOrdersList.items.addAll(Client.getClientOrders(client).getOr {
            val errorAlert = Alert(Alert.AlertType.ERROR)
            errorAlert.headerText = "Error"
            errorAlert.contentText = it
            errorAlert.showAndWait()
            init(client)
            return
        })

        existedOrdersList.setOnMouseClicked {
            val order = existedOrdersList.selectionModel.selectedItems.first()
            payButton.isVisible = order.status == OrderStatus.DONE && order.paymentStatus == PaymentStatus.NOT_PAID
        }

    }

    @FXML
    fun onNewOrderButtonClicked(actionEvent: ActionEvent) {
        existedOrdersButton.isVisible = false
        newOrderButton.isVisible = false
        productList.isVisible = true
        backButton.isVisible = true
        confirmOrderButton.isVisible = true
        selectedProductsForOrderList.isVisible = true

        val list = FXCollections.observableArrayList<Product>()
        productList.items = list
        productList.setCellFactory {
            object : ListCell<Product>() {
                override fun updateItem(item: Product?, empty: Boolean) {
                    super.updateItem(item, empty)
                    if (empty) text = null
                    else {
                        text = item?.name
                    }
                }
            }
        }

        val listForSelected = FXCollections.observableArrayList<Product>()
        selectedProductsForOrderList.items = listForSelected

        selectedProductsForOrderList.setCellFactory {
            object : ListCell<Product>() {
                override fun updateItem(item: Product?, empty: Boolean) {
                    super.updateItem(item, empty)
                    if (empty) text = null
                    else {
                        text = item?.name
                    }
                }
            }
        }

        transaction {
            list.addAll(Product.all().map { it })
        }

        productList.selectionModel.selectionMode = SelectionMode.MULTIPLE

        productList.setOnMouseClicked {
            listForSelected.add(productList.selectionModel.selectedItems.first())
        }
    }

    @FXML
    fun onLogoutButtonClicked(actionEvent: ActionEvent) {
        Vinoteka.showVinotekaView()
    }

    @FXML
    fun onBackButtonClicked(actionEvent: ActionEvent) {
        init(client)
    }

    @FXML
    fun onConfirmOrderButtonClicked(actionEvent: ActionEvent) {
        messageConfirmedOrderLabel.isVisible = true
        existedOrdersButton.isVisible = false
        newOrderButton.isVisible = false
        productList.isVisible = false
        backButton.isVisible = true
        confirmOrderButton.isVisible = false
        selectedProductsForOrderList.isVisible = false

        val orderDetail = selectedProductsForOrderList.items.groupBy { it }.mapValues { (_, v) -> v.size }
        Client.formOrder(client, orderDetail)
    }

    @FXML
    fun onPayButtonClicked(actionEvent: ActionEvent) {
        payButton.isVisible = false
        existedOrdersList.isVisible = false
        messagePaidOrderLabel.isVisible = true
        existedOrdersButton.isVisible = false
        newOrderButton.isVisible = false
        productList.isVisible = false
        backButton.isVisible = true
        confirmOrderButton.isVisible = false
        selectedProductsForOrderList.isVisible = false

        val order = existedOrdersList.selectionModel.selectedItems.first()
        Client.payOrder(client, order).getOr {
            val errorAlert = Alert(Alert.AlertType.ERROR)
            errorAlert.headerText = "Error"
            errorAlert.contentText = it
            errorAlert.showAndWait()
            init(client)
            return
        }
    }

}