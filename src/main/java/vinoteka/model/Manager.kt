package vinoteka.model

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import vinoteka.bp.BPFacadeManager
import vinoteka.bp.BPFacadeManagerImpl
import vinoteka.db.table.ManagerTable


class Manager constructor(id: EntityID<Int>) : IntEntity(id), ModelWithUser {
    override var user by UserImpl referencedOn ManagerTable.user

    companion object : IntEntityClass<Manager>(ManagerTable), BPFacadeManager by BPFacadeManagerImpl
}
