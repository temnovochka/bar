package vinoteka.bp

import vinoteka.model.*

interface BPFacadeManager {
    fun getClients(): BPResult<List<Client>>
    fun confirmClient(client: Client): BPResult<Client>
    fun confirmOrder(order: Order, manager: Manager): BPResult<Order>
    fun getManagerOrders(): BPResult<List<Order>>
    fun checkOrders(manager: Manager): BPResult<List<Order>>
    //    fun getManagerProducts(): BPResult<List<Stock>>
    fun makeManagerPurchase(intoPurchase: Map<Stock, Int>, manager: Manager): BPResult<Purchase>
}