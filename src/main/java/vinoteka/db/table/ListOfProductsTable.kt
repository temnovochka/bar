package vinoteka.db.table

import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object ListOfProductsTable : IntIdTable("list_of_products") {
    val product = reference("product_id", ProductTable, ReferenceOption.CASCADE)
    val order = reference("order_id", OrderTable, ReferenceOption.CASCADE).nullable()
    val purchase = reference("purchase_id", PurchaseTable, ReferenceOption.CASCADE).nullable()
    val number = integer("number")
}
