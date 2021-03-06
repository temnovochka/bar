package vinoteka.model

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import vinoteka.bp.BPFacadeClient
import vinoteka.bp.BPFacadeClientImpl
import vinoteka.db.table.ClientTable

class Client(id: EntityID<Int>) : IntEntity(id), ModelWithUser {
    override var user by UserImpl referencedOn ClientTable.user

    companion object : IntEntityClass<Client>(ClientTable), BPFacadeClient by BPFacadeClientImpl

    var document by ClientTable.document
    var birthday by ClientTable.birthday
    var isConfirmed by ClientTable.isConfirmed
}
