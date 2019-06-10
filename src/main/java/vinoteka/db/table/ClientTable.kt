package vinoteka.db.table

import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object ClientTable : IntIdTable("client") {
    val user = reference("user_id", UserTable, ReferenceOption.CASCADE)
    val document = varchar("document", 100)
    val birthday = date("birthday")
    val isConfirmed = bool("is_confirmed").default(false)
}
