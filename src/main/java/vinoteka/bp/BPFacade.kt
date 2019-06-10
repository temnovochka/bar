package vinoteka.bp

import org.joda.time.DateTime
import vinoteka.model.*

interface BPFacade {
    fun createClient(login: String, password: String, name: String, birthday: DateTime, document: String): BPResult<Client>
    fun createManager(login: String, password: String, name: String): BPResult<Manager>
    fun createAdmin(login: String, password: String, name: String): BPResult<Admin>

    fun formOrder(client: Client, products: Map<Product, Int>): BPResult<Order>
    fun getClientOrders(client: Client): BPResult<List<Order>>
    fun getUser(login: String, password: String): BPResult<User>
    fun payOrder(client: Client, order: Order): BPResult<Order>

    fun getClients(): BPResult<List<Client>>
    fun confirmClient(client: Client): BPResult<Client>
    fun confirmOrder(order: Order, manager: Manager): BPResult<Order>
    fun getManagerOrders(): BPResult<List<Order>>
    fun checkOrders(manager: Manager): BPResult<List<Order>>
//    fun getManagerProducts(): BPResult<List<Stock>>
    fun makeManagerPurchase(intoPurchase: Map<Stock, Int>, manager: Manager): BPResult<Purchase>
}
