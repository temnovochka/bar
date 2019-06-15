import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import vinoteka.bp.BPFacadeImpl.createAdmin
import vinoteka.bp.BPFacadeImpl.createManager
import vinoteka.db.table.*
import vinoteka.model.*

class PurchaseTest {

    @Test
    fun testCreateOrder() {
        println("test creating purchase")

        val userAdmin = transaction { UserImpl.find { UserTable.login eq "max" }.first() }
        val userManager = transaction { UserImpl.find { UserTable.login eq "anton" }.first() }
        val admin = transaction { Admin.find { AdminTable.user eq userAdmin.id }.first() }
        val manager = transaction { Manager.find { ManagerTable.user eq userManager.id }.first() }

        val products = transaction { Product.all().map { it.name to 5 }.toMap() }
        var purchase = Manager.makeManagerPurchase(products, manager).getOr {
            println(it)
            return assertFalse(true)
        }

        val productsInPurchase = transaction {
            ListOfProducts.find { ListOfProductsTable.purchase eq purchase.id }
                .map { it.product.name to it.number }.toMap()
        }

        assertEquals(OrderStatus.NEW, purchase.status)
        assertEquals(products, productsInPurchase)
        transaction { assertEquals(manager.login, purchase.manager.login) }
        assertNull(purchase.admin)

        val supplier = "supplier 1"
        purchase = Admin.saveChangesInPurchase(admin, purchase, supplier).getOr {
            println(it)
            return assertFalse(true)
        }

        assertEquals(OrderStatus.IN_PROGRESS, purchase.status)
        assertEquals(supplier, purchase.supplier)
        transaction { assertEquals(admin.login, purchase.admin!!.login) }

        purchase = Admin.makePurchaseDone(admin, purchase).getOr {
            println(it)
            return assertFalse(true)
        }

        assertEquals(OrderStatus.DONE, purchase.status)
        assertFalse(purchase.isAddedIntoStock)

        Manager.putDonePurchasesIntoStock(manager).getOr {
            println(it)
            return assertFalse(true)
        }

        purchase = transaction { Purchase.find { PurchaseTable.id eq purchase.id }.first() }
        assertTrue(purchase.isAddedIntoStock)
    }

    companion object {
        @BeforeAll
        @JvmStatic
        fun beforeAll() {
            println("beforeAll")
            val testDatabase =
                Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false;", "org.h2.Driver")
            initDb(testDatabase)

            val manager = createManager("anton", "qwerty", "Anton").getOr {
                println(it)
                return
            }
            val admin = createAdmin("max", "qwerty", "Max").getOr {
                println(it)
                return
            }

            val aperol = transaction { Product.new {
                name="aperol"
                price=1000.0
            } }

            val martini = transaction { Product.new {
                name="martini"
                price=800.0
            } }

            val stock1 = transaction { Stock.new {
                product = martini
                number = 10
                dateUpdate = DateTime.now()
                this.manager = manager
            } }

            val stock2 = transaction { Stock.new {
                product = aperol
                number = 3
                dateUpdate = DateTime.now()
                this.manager = manager
            } }

        }

        private fun initDb(testDatabase: Database) = transaction(testDatabase) {
            SchemaUtils.create(UserTable, AdminTable, ClientTable, ManagerTable)
            SchemaUtils.create(OrderTable, ProductTable, PurchaseTable, StockTable, ListOfProductsTable)
        }
    }

}