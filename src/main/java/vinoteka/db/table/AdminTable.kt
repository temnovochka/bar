package vinoteka.db.table

import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object AdminTable : IntIdTable("admin") {
    val user = reference("user_id", UserTable, ReferenceOption.CASCADE)
}
