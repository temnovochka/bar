package vinoteka.bp

import org.joda.time.DateTime
import vinoteka.db.table.ListOfProductsTable
import vinoteka.model.Admin
import vinoteka.model.ListOfProducts
import vinoteka.model.OrderStatus
import vinoteka.model.Purchase
import java.lang.Exception

object BPFacadeAdminImpl : BPFacadeAdmin {
    override fun makePurchaseDone(admin: Admin, purchase: Purchase) = bpTransaction {
        if (purchase.status != OrderStatus.IN_PROGRESS) {
            throw Exception("This purchase is not IN_PROGRESS, so you can't make it done")
        }
        purchase.admin = admin
        purchase.status = OrderStatus.DONE
        purchase.executionDate = DateTime.now()
        purchase
    }

    override fun saveChangesInPurchase(admin: Admin, purchase: Purchase, supplier: String) = bpTransaction {
        if (supplier.isEmpty()) {
            throw Exception("Please, input supplier")
        }
        purchase.admin = admin
        purchase.status = OrderStatus.IN_PROGRESS
        purchase.supplier = supplier
        purchase
    }

    override fun getPurchaseStatuses() = bpTransaction {
        OrderStatus.values().map { "$it" }
    }

    override fun getDetailInfoOfPurchase(purchase: Purchase) = bpTransaction {
        ListOfProducts.find { ListOfProductsTable.purchase eq purchase.id}.map { "${it.product.name} - ${it.number}" }
    }

    override fun getAllPurchases() = bpTransaction {
        Purchase.all().toList()
    }
}