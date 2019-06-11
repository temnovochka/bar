package vinoteka.bp

import org.joda.time.DateTime
import vinoteka.db.table.OrderTable
import vinoteka.model.*

object BPFacadeClientImpl : BPFacadeClient {
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
