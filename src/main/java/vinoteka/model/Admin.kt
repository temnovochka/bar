package vinoteka.model

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import vinoteka.db.table.AdminTable


class Admin constructor(id: EntityID<Int>) : IntEntity(id), ModelWithUser {
    override var user by UserImpl referencedOn AdminTable.user

    companion object : IntEntityClass<Admin>(AdminTable)
}