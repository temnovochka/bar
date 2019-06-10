package vinoteka.db.table

import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object ManagerTable : IntIdTable("manager") {
    val user = reference("user_id", UserTable, ReferenceOption.CASCADE)
}
