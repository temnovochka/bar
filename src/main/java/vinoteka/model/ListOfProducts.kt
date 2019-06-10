package vinoteka.model

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import vinoteka.db.table.ListOfProductsTable

class ListOfProducts(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ListOfProducts>(ListOfProductsTable)

    var product by Product referencedOn ListOfProductsTable.product
    var order by Order optionalReferencedOn ListOfProductsTable.order
    var purchase by Purchase optionalReferencedOn ListOfProductsTable.purchase
    var number by ListOfProductsTable.number
}
