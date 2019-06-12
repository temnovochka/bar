package vinoteka.db.table

import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import vinoteka.model.OrderStatus

object PurchaseTable : IntIdTable("purchase") {
    val manager = reference("manager_id", ManagerTable, ReferenceOption.CASCADE)
    val admin = reference("admin_id", AdminTable, ReferenceOption.CASCADE).nullable()
    val supplier = varchar("supplier", 100).nullable()
    val formedDate = date("formed_date")
    val executionDate = date("execution_date").nullable()
    val status = enumerationByName("status", 20, OrderStatus::class)
    val isAddedIntoStock = bool("is_added_into_stock").default(false)
}
