package vinoteka.bp

import org.joda.time.DateTime
import vinoteka.model.*

interface BPFacade {
    fun createClient(login: String, password: String, name: String, birthday: DateTime, document: String): BPResult<Client>
    fun createManager(login: String, password: String, name: String): BPResult<Manager>
    fun createAdmin(login: String, password: String, name: String): BPResult<Admin>

    fun getUser(login: String, password: String): BPResult<User>
}
