package vinoteka.bp

import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import vinoteka.db.table.ListOfProductsTable
import vinoteka.db.table.OrderTable
import vinoteka.model.*

object BPFacadeClientImpl : BPFacadeClient {
    override fun getAllBarProducts() = bpTransaction {
        Product.all().map { it }
    }

    override fun isOrderForPay(order: Order) = bpTransaction {
        order.status == OrderStatus.DONE && order.paymentStatus == PaymentStatus.NOT_PAID
    }

    override fun isConfirmed(client: Client) = bpTransaction {
        client.isConfirmed
    }

    override fun getOrderDetail(client: Client, order: Order): BPResult<List<String>> {
        val res = mutableListOf<String>()
        transaction {
            val productsInOrder = ListOfProducts.find { ListOfProductsTable.order eq order.id }.toList()
            for (prod in productsInOrder) {
                res.add("${prod.product.name} - ${prod.number}")
            }
        }
        return res.success()
    }

    override fun getClientOrders(client: Client) = bpTransaction {
        Order.find { OrderTable.client eq client.id }.toList()
    }

    override fun payOrder(client: Client, order: Order) = bpTransaction {
        if (client.id != order.client.id) {
            throw Exception("You are not an order owner")
        }
        order.paymentStatus = PaymentStatus.PAID
        order
    }

    override fun formOrder(client: Client, products: Map<Product, Int>): BPResult<Order> {
        if (!client.isConfirmed) {
            return "Your record is not confirmed".fail()
        }
        return bpTransaction {
            val order = Order.new {
                this.client = client
                this.manager = null
                this.executionDate = null
                this.status = OrderStatus.NEW
                this.paymentStatus = PaymentStatus.NOT_PAID
                this.registerDate = DateTime.now()
            }
            for ((prod, num) in products) {
                ListOfProducts.new {
                    this.order = order
                    this.number = num
                    this.product = prod
                }
            }
            order
        }
    }
}
