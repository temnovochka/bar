package vinoteka.model

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import vinoteka.db.table.ProductTable

class Product(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Product>(ProductTable)

    var name by ProductTable.name
    var features by ProductTable.features
    var price by ProductTable.price
}
