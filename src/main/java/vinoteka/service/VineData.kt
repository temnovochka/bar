package vinoteka.service

import com.google.gson.Gson
import org.jetbrains.exposed.sql.transactions.transaction
import vinoteka.model.Product
import java.net.HttpURLConnection
import java.net.URL
import kotlin.random.Random


data class Wine(val wine: String, val color: String, val wine_type: String)
data class Result(val count: Int, val next: String, val results: List<Wine>)

object VineData {
    val apiKey = "5c245db6b7a00e40e648617d2a9ad69d349094b7"

    fun fetchData(): List<Wine> {
        val url = URL("https://api.globalwinescore.com/globalwinescores/latest/?limit=10")

        val response = with(url.openConnection() as HttpURLConnection) {
            requestMethod = "GET"
            addRequestProperty("Authorization", "Token $apiKey")

            inputStream.bufferedReader().use {
                it.readText()
            }
        }

        val data = Gson().fromJson<Result>(response, Result::class.java)
        return data.results
    }

    fun actualizeProducts() {
        val wine = try {
            fetchData()
        } catch (ex: Exception) {
            return
        }
        val products = transaction {
            Product.all().map { it.name }
        }
        val newWine = wine.filter { it.wine !in products }
        transaction {
            newWine.forEach { wine ->
                Product.new {
                    name = wine.wine
                    features = "${wine.color}, ${wine.wine_type}"
                    price = Random.nextDouble(10.0, 100.0)
                }
            }
        }
    }
}

