package vinoteka.model

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import vinoteka.db.table.PurchaseTable

class Purchase(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Purchase>(PurchaseTable)

    var manager by Manager referencedOn PurchaseTable.manager
    var admin by Admin optionalReferencedOn PurchaseTable.admin
    var supplier by PurchaseTable.supplier
    var formedDate by PurchaseTable.formedDate
    var executionDate by PurchaseTable.executionDate
    var status by PurchaseTable.status
}
