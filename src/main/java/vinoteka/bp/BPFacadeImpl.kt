package vinoteka.bp

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import vinoteka.db.table.*
import vinoteka.model.*
import java.util.*

object BPFacadeImpl : BPFacade {
    override fun makeManagerPurchase(intoPurchase: Map<Stock, Int>, manager: Manager)= bpTransaction {
        val purchase = Purchase.new {
            this.manager = manager
            this.executionDate = null
            this.formedDate = DateTime.now()
            this.status = OrderStatus.NEW
        }

        for ((stock, num) in intoPurchase) {
            val listOfProd = ListOfProducts.new {
                this.product = stock.product
                this.number = num
                this.purchase = purchase
            }
        }

        purchase
    }

    override fun getManagerOrders() = bpTransaction {
        Order.find { OrderTable.status eq OrderStatus.NEW }.toList()
    }

    override fun confirmOrder(order: Order, manager: Manager) = bpTransaction {
        order.status = OrderStatus.IN_PROGRESS
        order.manager = manager
        order
    }

    override fun checkOrders(manager: Manager) = bpTransaction {
        val orders = Order.find { OrderTable.status eq OrderStatus.IN_PROGRESS }.toList()

        for (order in orders) {
            val prodInOrder = ListOfProducts.find { ListOfProductsTable.order eq order.id }.map { it.product to it.number }
            val stock = Stock.all().map { it.product to it.number }.toMap()

            val newNumberPerProduct = mutableMapOf<Stock, Int>()

            for ((prod, num) in prodInOrder) {
                val numInStock = stock.getOrDefault(prod, 0)
                if (num <= numInStock) {
                    val stockProd = Stock[prod.id]
                    newNumberPerProduct[stockProd] = stockProd.number - num
                } else {
                    order.status = OrderStatus.NOT_DONE
                    order.executionDate = DateTime.now()
                    continue
                }
            }

            for ((oneStock, num) in newNumberPerProduct) {
                oneStock.number = num
                oneStock.manager = manager
                oneStock.dateUpdate = DateTime.now()
            }

            order.status = OrderStatus.DONE
            order.executionDate = DateTime.now()
        }
        orders
    }

    override fun confirmClient(client: Client) = bpTransaction {
        client.isConfirmed = true
        client
    }

    override fun getClients() = bpTransaction {
        Client.find { ClientTable.isConfirmed eq false }.toList()
    }

    override fun payOrder(client: Client, order: Order) = bpTransaction {
        if (client.id != order.client.id) {
            throw Exception("You are not an order owner")
        }
        order.paymentStatus = PaymentStatus.PAID
        order
    }

    override fun getClientOrders(client: Client) = bpTransaction {
        Order.find { OrderTable.client eq client.id }.toList()
    }

    override fun formOrder(client: Client, products: Map<Product, Int>): BPResult<Order> {
        if (!client.isConfirmed) {
            return "Your record is not confirmed".fail()
        }
        return bpTransaction {
            val order = Order.new {
                this.client = client
                this.manager = null
                this.executionDate = null
                this.status = OrderStatus.NEW
                this.paymentStatus = PaymentStatus.NOT_PAID
                this.registerDate = DateTime.now()
            }
            for ((prod, num) in products) {
                ListOfProducts.new {
                    this.order = order
                    this.number = num
                    this.product = prod
                }
            }
            order
        }
    }

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

    override fun createClient(
        login: String,
        password: String,
        name: String,
        birthday: DateTime,
        document: String
    ): BPResult<Client> =
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