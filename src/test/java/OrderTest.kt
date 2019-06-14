import io.mockk.unmockkAll
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.junit.jupiter.api.AfterAll
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
        println("test 1")

        val birthday = DateTime(1990, 6, 16, 0, 0, 0)
        val client = createClient("alice", "qwerty", "Alice", birthday, "passport").getOr {
            println(it)
            return
        }
        val manager = createManager("anton", "qwerty", "Anton").getOr {
            println(it)
            return
        }

        assertFalse(client.isConfirmed)
        Manager.confirmClient(client)
        assertTrue(client.isConfirmed)

        val prod = transaction { Product.new {
            name="aperol"
            price=100.0
        } }

        val products = transaction { Product.all().map { it to 5 }.toMap() }
        var order = Client.formOrder(client, products).getOr {
            println(it)
            return assertFalse(true)
        }

        assertEquals(OrderStatus.NEW, order.status)
        assertEquals(PaymentStatus.NOT_PAID, order.paymentStatus)
        transaction { assertEquals(client.id, order.client.id) }
        assertNull(order.manager)

        order = Manager.confirmOrder(order, manager).getOr {
            println(it)
            return assertFalse(true)
        }
        assertEquals(OrderStatus.IN_PROGRESS, order.status)
        transaction { assertEquals(manager.id, order.manager!!.id) }

        order = Manager.checkOrders(manager).getOr {
            println(it)
            return assertFalse(true)
        }.first()
        assertEquals(OrderStatus.NOT_DONE, order.status)
    }

//    @Test
//    fun testCreateOrder2() {
//        println("test 2")
//    }
//
//
//    @BeforeEach
//    fun before() {
//        println("before")
//    }
//
//    @AfterEach
//    fun after() {
//        println("after")
//    }


    companion object {
        @BeforeAll
        @JvmStatic
        fun beforeAll() {
            println("beforeAll")
            val testDatabase =
                Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false;", "org.h2.Driver")
            initDb(testDatabase)
        }

        private fun initDb(testDatabase: Database) = transaction(testDatabase) {
            SchemaUtils.create(UserTable, AdminTable, ClientTable, ManagerTable)
            SchemaUtils.create(OrderTable, ProductTable, PurchaseTable, StockTable, ListOfProductsTable)
        }

        @AfterAll
        @JvmStatic
        fun afterAll() {
            println("afterAll")
            unmockkAll()
        }
    }

}