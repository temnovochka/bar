package vinoteka.db.table

import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import vinoteka.model.OrderStatus
import vinoteka.model.PaymentStatus

object OrderTable : IntIdTable("product_order") {
    val client = reference("client_id", ClientTable, ReferenceOption.CASCADE)
    val manager = reference("manager_id", ManagerTable, ReferenceOption.CASCADE).nullable()
    val registerDate = date("register_date")
    val executionDate = date("execution_date").nullable()
    val status = enumerationByName("status", 20, OrderStatus::class)
    val paymentStatus = enumerationByName("payment_status", 20, PaymentStatus::class)
}
