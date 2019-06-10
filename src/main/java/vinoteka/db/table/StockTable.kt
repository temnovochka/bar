package vinoteka.db.table

import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object StockTable : IntIdTable("stock") {
    val product = reference("product_id", ProductTable, ReferenceOption.CASCADE)
    val number = integer("number")
    val manager = reference("manager_id", ManagerTable, ReferenceOption.CASCADE)
    val dateUpdate = date("date_update")
}
