package vinoteka.bp

import vinoteka.model.Admin
import vinoteka.model.Purchase

interface BPFacadeAdmin {
    fun getAllPurchases(): BPResult<List<Purchase>>
    fun getDetailInfoOfPurchase(purchase: Purchase): BPResult<List<String>>
    fun getPurchaseStatuses(): BPResult<List<String>>
    fun saveChangesInPurchase(admin: Admin, purchase: Purchase, supplier: String): BPResult<Purchase>
    fun makePurchaseDone(admin: Admin, purchase: Purchase): BPResult<Purchase>
}