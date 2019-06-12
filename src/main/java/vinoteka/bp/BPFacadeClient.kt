package vinoteka.bp

import vinoteka.model.Client
import vinoteka.model.Order
import vinoteka.model.Product

interface BPFacadeClient {
    fun formOrder(client: Client, products: Map<Product, Int>): BPResult<Order>
    fun getClientOrders(client: Client): BPResult<List<Order>>
    fun payOrder(client: Client, order: Order): BPResult<Order>
    fun getOrderDetail(client: Client, order: Order): BPResult<List<String>>
    fun isConfirmed(client: Client): BPResult<Boolean>
    fun isOrderForPay(order: Order): BPResult<Boolean>
    fun getAllBarProducts(): BPResult<List<Product>>
}
