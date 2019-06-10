package vinoteka.db.table

import org.jetbrains.exposed.dao.IntIdTable

object ProductTable : IntIdTable("product") {
    val name = varchar("name", 100)
    val features = text("features").nullable()
    val price = double("price")
}
