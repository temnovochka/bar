import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import vinoteka.bp.BPFacadeImpl.createClient
import vinoteka.bp.BPFacadeImpl.createManager
import vinoteka.db.table.*
import vinoteka.model.*

class OrderTest {

    @Test
    fun testCreateOrder() {
        println("test creating order")

        val userClient = transaction { UserImpl.find { UserTable.login eq "alice" }.first() }
        val userManager = transaction { UserImpl.find { UserTable.login eq "anton" }.first() }
        val client = transaction { Client.find { ClientTable.user eq userClient.id }.first() }
        val manager = transaction { Manager.find { ManagerTable.user eq userManager.id }.first() }

        assertFalse(client.isConfirmed)
        Manager.confirmClient(client)
        assertTrue(client.isConfirmed)

        // order, for which there are NOT enough products in the stock

        val productsBig = transaction { Product.all().map { it to 5 }.toMap() }
        var orderBig = Client.formOrder(client, productsBig).getOr {
            println(it)
            return assertFalse(true)
        }

        val startStockNumbers = transaction { Stock.all().map { it.product.name to it.number }.toMap() }

        assertEquals(OrderStatus.NEW, orderBig.status)
        assertEquals(PaymentStatus.NOT_PAID, orderBig.paymentStatus)
        transaction { assertEquals(client.id, orderBig.client.id) }
        assertNull(orderBig.manager)

        orderBig = Manager.confirmOrder(orderBig, manager).getOr {
            println(it)
            return assertFalse(true)
        }
        assertEquals(OrderStatus.IN_PROGRESS, orderBig.status)
        transaction { assertEquals(manager.id, orderBig.manager!!.id) }

        orderBig = Manager.checkOrders(manager).getOr {
            println(it)
            return assertFalse(true)
        }.first()
        assertEquals(OrderStatus.NOT_DONE, orderBig.status)

        val firstStockNumber = transaction { Stock.all().map { it.product.name to it.number }.toMap(mutableMapOf()) }
        assertEquals(startStockNumbers, firstStockNumber)

        // order, for which there are enough products in the stock

        val productsSmall = transaction { Product.all().map { it to 1 }.toMap() }
        var orderSmall = Client.formOrder(client, productsSmall).getOr {
            println(it)
            return assertFalse(true)
        }

        assertEquals(OrderStatus.NEW, orderSmall.status)
        assertEquals(PaymentStatus.NOT_PAID, orderSmall.paymentStatus)
        transaction { assertEquals(client.id, orderSmall.client.id) }
        assertNull(orderSmall.manager)

        orderSmall = Manager.confirmOrder(orderSmall, manager).getOr {
            println(it)
            return assertFalse(true)
        }
        assertEquals(OrderStatus.IN_PROGRESS, orderSmall.status)
        transaction { assertEquals(manager.id, orderSmall.manager!!.id) }

        orderSmall = Manager.checkOrders(manager).getOr {
            println(it)
            return assertFalse(true)
        }.first()
        assertEquals(OrderStatus.DONE, orderSmall.status)

        val secondStockNumber = transaction { Stock.all().map { it.product to it.number }.toMap(mutableMapOf()) }
        for ((prod, num) in secondStockNumber) {
            secondStockNumber[prod] = num + productsSmall.getOrDefault(prod, 0)
        }
        assertEquals(startStockNumbers, firstStockNumber)
    }

    companion object {
        @BeforeAll
        @JvmStatic
        fun beforeAll() {
            println("beforeAll")
            val testDatabase =
                Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false;", "org.h2.Driver")
            initDb(testDatabase)

            val birthday = DateTime(1990, 6, 16, 0, 0, 0)
            val client = createClient("alice", "qwerty", "Alice", birthday, "passport").getOr {
                println(it)
                return
            }
            val manager = createManager("anton", "qwerty", "Anton").getOr {
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