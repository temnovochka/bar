package vinoteka.model

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import vinoteka.db.table.OrderTable

class Order(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Order>(OrderTable)

    var client by Client referencedOn OrderTable.client
    var manager by Manager optionalReferencedOn OrderTable.manager
    var registerDate by OrderTable.registerDate
    var executionDate by OrderTable.executionDate
    var status by OrderTable.status
    var paymentStatus by OrderTable.paymentStatus
}
