package vinoteka.db

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import vinoteka.db.table.*
import vinoteka.model.*
import java.util.*

object Db {
    fun init() {
        Database.connect("jdbc:postgresql://localhost:5432/vinoteka", "org.postgresql.Driver", "postgres", "postgres")
        actualizeSchema()
    }

    private fun actualizeSchema() = transaction {
        SchemaUtils.createMissingTablesAndColumns(UserTable, AdminTable, ClientTable, ManagerTable)
        SchemaUtils.createMissingTablesAndColumns(
            OrderTable,
            ProductTable,
            PurchaseTable,
            StockTable,
            ListOfProductsTable
        )
    }
}

fun main() {
    Db.init()
    transaction {
        val userAdmin = UserImpl.new {
            login = "admin"
            password = "admin"
            role = UserRole.ADMIN
            name = "Alex"
        }

        val userManager = UserImpl.new {
            login = "manager"
            password = "manager"
            role = UserRole.MANAGER
            name = "Maria"
        }

        val userClient = UserImpl.new {
            login = "client"
            password = "client"
            role = UserRole.CLIENT
            name = "Vasya"
        }

        val admin = Admin.new {
            this.user = userAdmin
        }
        val manager = Manager.new { user = userManager }
        val client = Client.new {
            user = userClient
            birthday = DateTime(Calendar.Builder().setDate(1975, 7, 18).build().time)
            document = "passport 1234 567890"
        }

        val aperol = Product.new {
            name = "Aperol"
            features = "sweet aperitive"
            price = 1200.0
        }
        val luhny = Product.new {
            name = "Luhny"
            features = "semisweet red wine"
            price = 485.0
        }
        val psoy = Product.new {
            name = "Psoy"
            features = "semisweet white wine"
            price = 476.0
        }

        val stock1 = Stock.new {
            product = aperol
            number = 10
            this.manager = manager
            dateUpdate = DateTime(Calendar.Builder().setDate(2019, 6, 6).build().time)
        }

        val stock2 = Stock.new {
            product = luhny
            number = 10
            this.manager = manager
            dateUpdate = DateTime(Calendar.Builder().setDate(2019, 6, 6).build().time)
        }

        val stock3 = Stock.new {
            product = psoy
            number = 10
            this.manager = manager
            dateUpdate = DateTime(Calendar.Builder().setDate(2019, 6, 6).build().time)
        }
    }
}