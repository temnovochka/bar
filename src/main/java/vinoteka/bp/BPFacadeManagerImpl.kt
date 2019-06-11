package vinoteka.bp

import org.joda.time.DateTime
import vinoteka.db.table.ClientTable
import vinoteka.db.table.ListOfProductsTable
import vinoteka.db.table.OrderTable
import vinoteka.model.*

object BPFacadeManagerImpl : BPFacadeManager {
    override fun getClients() = bpTransaction {
        Client.find { ClientTable.isConfirmed eq false }.toList()
    }

    override fun confirmClient(client: Client) = bpTransaction {
        client.isConfirmed = true
        client
    }

    override fun confirmOrder(order: Order, manager: Manager) = bpTransaction {
        order.status = OrderStatus.IN_PROGRESS
        order.manager = manager
        order
    }

    override fun getManagerOrders() = bpTransaction {
        Order.find { OrderTable.status eq OrderStatus.NEW }.toList()
    }

    override fun checkOrders(manager: Manager) = bpTransaction {
        val orders = Order.find { OrderTable.status eq OrderStatus.IN_PROGRESS }.toList()

        for (order in orders) {
            val prodInOrder =
                ListOfProducts.find { ListOfProductsTable.order eq order.id }.map { it.product to it.number }
            val stock = Stock.all().map { it.product to it.number }.toMap()

            val newNumberPerProduct = mutableMapOf<Stock, Int>()
            var isNotEnough = false

            for ((prod, num) in prodInOrder) {
                val numInStock = stock.getOrDefault(prod, 0)
                if (num <= numInStock) {
                    newNumberPerProduct[Stock[prod.id]] = Stock[prod.id].number - num
                } else {
                    isNotEnough = true
                    break
                }
            }

            order.executionDate = DateTime.now()

            when (isNotEnough) {
                true -> order.status = OrderStatus.NOT_DONE
                else -> {
                    order.status = OrderStatus.DONE

                    for ((oneStock, num) in newNumberPerProduct) {
                        oneStock.number = num
                        oneStock.manager = manager
                        oneStock.dateUpdate = DateTime.now()
                    }
                }
            }
        }
        orders
    }

    override fun makeManagerPurchase(intoPurchase: Map<Stock, Int>, manager: Manager) = bpTransaction {
        val purchase = Purchase.new {
            this.manager = manager
            this.formedDate = DateTime.now()
            this.status = OrderStatus.NEW
        }

        for ((stock, num) in intoPurchase) {
            val listOfProd = ListOfProducts.new {
                this.product = stock.product
                this.number = num
                this.purchase = purchase
            }
        }

        purchase
    }
}