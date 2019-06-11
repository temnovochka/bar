package vinoteka.bp

import org.jetbrains.exposed.sql.and
import org.joda.time.DateTime
import vinoteka.db.table.*
import vinoteka.model.*

object BPFacadeImpl : BPFacade {
    override fun getUser(login: String, password: String): BPResult<User> {
        val user = bpTransaction {
            UserImpl.find { UserTable.login eq login and (UserTable.password eq password) }.singleOrNull()
        }.getOr { return it.fail() }
            ?: return "User not found".fail()
        return when (user.role) {
            UserRole.MANAGER -> bpTransaction {
                Manager.find { ManagerTable.user eq user.id }.single()
            }
            UserRole.CLIENT -> bpTransaction {
                Client.find { ClientTable.user eq user.id }.single()
            }
            UserRole.ADMIN -> bpTransaction {
                Admin.find { AdminTable.user eq user.id }.single()
            }
        }
    }

    private fun creteUser(login: String, password: String, name: String, role: UserRole): UserImpl {
        return UserImpl.new {
            this.login = login
            this.password = password
            this.name = name
            this.role = role
        }
    }

    override fun createClient(login: String, password: String, name: String, birthday: DateTime, document: String) =
        bpTransaction {
            if (!UserImpl.find { UserTable.login eq login }.empty())
                throw Exception("This login is in use")
            val user = creteUser(login, password, name, UserRole.CLIENT)
            Client.new {
                this.user = user
                this.birthday = birthday
                this.document = document
            }
        }

    override fun createAdmin(login: String, password: String, name: String): BPResult<Admin> =
        bpTransaction {
            val user = creteUser(login, password, name, UserRole.ADMIN)
            Admin.new {
                this.user = user
            }
        }

    override fun createManager(login: String, password: String, name: String): BPResult<Manager> =
        bpTransaction {
            val user = creteUser(login, password, name, UserRole.MANAGER)
            Manager.new {
                this.user = user
            }
        }

}