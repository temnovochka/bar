package vinoteka.model

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import vinoteka.db.table.UserTable

class UserImpl(id: EntityID<Int>) : IntEntity(id), User {
    companion object : IntEntityClass<UserImpl>(UserTable)

    override var name by UserTable.name
    override var login by UserTable.login
    override var password by UserTable.password
    override var role by UserTable.role
}