package vinoteka.db.table

import org.jetbrains.exposed.dao.IntIdTable
import vinoteka.model.UserRole

object UserTable : IntIdTable("user") {
    val login = varchar("login", 20)
    val password = varchar("password", 20)
    val role = enumerationByName("role", 20, UserRole::class)
    val name = varchar("name", 50)
}
