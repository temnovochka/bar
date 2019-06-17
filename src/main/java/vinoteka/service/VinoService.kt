package vinoteka.service

import com.google.gson.Gson
import com.sun.net.httpserver.HttpServer
import org.jetbrains.exposed.sql.transactions.transaction
import vinoteka.model.Product
import java.io.PrintWriter
import java.net.InetSocketAddress

object VinoService {

    private val gson = Gson()
    private lateinit var server: HttpServer

    fun start() {
        server = HttpServer.create(InetSocketAddress(8080), 0).apply {
            createContext("/wine") { http ->
                val products = transaction {
                    Product.all().map { mapOf("name" to it.name, "features" to it.features, "price" to it.price) }
                }
                val jsonResult = gson.toJson(products)
                http.responseHeaders.add("Content-type", "application/json")
                http.sendResponseHeaders(200, 0)
                PrintWriter(http.responseBody).use { out ->
                    out.print(jsonResult)
                }
            }
            start()
        }
    }

    fun stop() {
        server.stop(0)
    }
}
