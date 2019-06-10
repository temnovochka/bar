package vinoteka.model

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.sql.ReferenceOption
import vinoteka.db.table.ManagerTable
import vinoteka.db.table.ProductTable
import vinoteka.db.table.PurchaseTable
import vinoteka.db.table.StockTable

class Stock(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Stock>(StockTable)

    var product by Product referencedOn StockTable.product
    var number by StockTable.number
    var manager by Manager referencedOn StockTable.manager
    var dateUpdate by StockTable.dateUpdate
}
