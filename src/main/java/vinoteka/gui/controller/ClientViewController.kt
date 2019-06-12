package vinoteka.gui.controller

import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.*
import vinoteka.gui.Vinoteka
import vinoteka.model.Client
import vinoteka.model.Order
import vinoteka.model.Product


class ClientViewController {
    lateinit var client: Client

    @FXML lateinit var existedOrdersButton: Button
    @FXML lateinit var newOrderButton: Button
    @FXML lateinit var logoutButton: Button
    @FXML lateinit var mainMenuButton: Button
    @FXML lateinit var confirmOrderButton: Button
    @FXML lateinit var payButton: Button
    @FXML lateinit var orderDetailButton: Button
    @FXML lateinit var backFromDetailButton: Button

    @FXML lateinit var selectedProductsForOrderList: ListView<Product>
    @FXML lateinit var productList: ListView<Product>
    @FXML lateinit var existedOrdersList: ListView<Order>
    @FXML lateinit var orderDetailList: ListView<String>

    @FXML lateinit var messageLabel: Label
    @FXML lateinit var messageConfirmedOrderLabel: Label
    @FXML lateinit var messagePaidOrderLabel: Label

    fun init(client: Client) {
        this.client = client
        existedOrdersButton.isVisible = true
        newOrderButton.isVisible = true
        productList.isVisible = false
        existedOrdersList.isVisible = false
        messageLabel.isVisible = false
        mainMenuButton.isVisible = false
        confirmOrderButton.isVisible = false
        selectedProductsForOrderList.isVisible = false
        payButton.isVisible = false
        messageConfirmedOrderLabel.isVisible = false
        messagePaidOrderLabel.isVisible = false
        orderDetailButton.isVisible = false
        orderDetailList.isVisible = false
        backFromDetailButton.isVisible = false

        if (!Client.isConfirmed(client).getOr {
                val errorAlert = Alert(Alert.AlertType.ERROR)
                errorAlert.headerText = "Error"
                errorAlert.contentText = it
                errorAlert.showAndWait()
                return
            }) {
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
        mainMenuButton.isVisible = true
        orderDetailButton.isVisible = true

        val list = FXCollections.observableArrayList<Order>()
        existedOrdersList.items = list

        existedOrdersList.setCellFactory {
            object : ListCell<Order>() {
                override fun updateItem(item: Order?, empty: Boolean) {
                    super.updateItem(item, empty)
                    text = when {
                        empty -> null
                        else -> "order_id = ${item?.id}, status = ${item?.status}, paid = ${item?.paymentStatus}"
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
            payButton.isVisible = Client.isOrderForPay(order).getOr { return@setOnMouseClicked }
        }

    }

    @FXML
    fun onNewOrderButtonClicked(actionEvent: ActionEvent) {
        existedOrdersButton.isVisible = false
        newOrderButton.isVisible = false
        productList.isVisible = true
        mainMenuButton.isVisible = true
        confirmOrderButton.isVisible = true
        selectedProductsForOrderList.isVisible = true

        val list = FXCollections.observableArrayList<Product>()
        productList.items = list
        productList.setCellFactory {
            object : ListCell<Product>() {
                override fun updateItem(item: Product?, empty: Boolean) {
                    super.updateItem(item, empty)
                    text = when {
                        empty -> null
                        else -> item?.name
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
                    text = when {
                        empty -> null
                        else -> item?.name
                    }
                }
            }
        }

        list.addAll(
            Client.getAllBarProducts().getOr {
                val errorAlert = Alert(Alert.AlertType.ERROR)
                errorAlert.headerText = "Error"
                errorAlert.contentText = it
                errorAlert.showAndWait()
                init(client)
                return
            })

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
    fun onMainMenuButtonClicked(actionEvent: ActionEvent) {
        init(client)
    }

    @FXML
    fun onConfirmOrderButtonClicked(actionEvent: ActionEvent) {
        messageConfirmedOrderLabel.isVisible = true
        existedOrdersButton.isVisible = false
        newOrderButton.isVisible = false
        productList.isVisible = false
        mainMenuButton.isVisible = true
        confirmOrderButton.isVisible = false
        selectedProductsForOrderList.isVisible = false

        val orderDetail = selectedProductsForOrderList.items.groupBy { it }.mapValues { (_, v) -> v.size }
        Client.formOrder(client, orderDetail).getOr {
            val errorAlert = Alert(Alert.AlertType.ERROR)
            errorAlert.headerText = "Error"
            errorAlert.contentText = it
            errorAlert.showAndWait()
            init(client)
            return
        }
    }

    @FXML
    fun onPayButtonClicked(actionEvent: ActionEvent) {
        payButton.isVisible = false
        existedOrdersList.isVisible = false
        messagePaidOrderLabel.isVisible = true
        existedOrdersButton.isVisible = false
        newOrderButton.isVisible = false
        productList.isVisible = false
        mainMenuButton.isVisible = true
        confirmOrderButton.isVisible = false
        selectedProductsForOrderList.isVisible = false
        orderDetailButton.isVisible = false

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

    fun onOrderDetailButtonClicked(actionEvent: ActionEvent) {
        orderDetailButton.isVisible = false
        existedOrdersList.isVisible = false
        orderDetailList.isVisible = true
        backFromDetailButton.isVisible = true

        val order = existedOrdersList.selectionModel.selectedItems.first()
        val res = Client.getOrderDetail(client, order).getOr {
            val errorAlert = Alert(Alert.AlertType.ERROR)
            errorAlert.headerText = "Error"
            errorAlert.contentText = it
            errorAlert.showAndWait()
            init(client)
            return
        }

        orderDetailList.items = FXCollections.observableArrayList(res)
    }

    fun onBackFromDetailButtonClicked(actionEvent: ActionEvent) {
        orderDetailList.isVisible = false
        backFromDetailButton.isVisible = false
        onExistedOrdersButtonClicked(actionEvent)
    }

}