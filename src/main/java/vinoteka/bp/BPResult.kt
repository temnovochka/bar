package vinoteka.bp

import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.transaction

sealed class BPResult<T> {
    data class Success<T>(val result: T) : BPResult<T>()
    data class Fail<T>(val message: String) : BPResult<T>()

    fun <R> handle(onSuccess: (T) -> R, onFail: (String) -> R): R = when (this) {
        is Success -> onSuccess(result)
        is Fail -> onFail(message)
    }

    inline fun getOr(onError: (String) -> Nothing): T = when (this) {
        is Success -> result
        is Fail -> onError(message)
    }
}

fun <T> String.fail() = BPResult.Fail<T>(this)
fun <T : Any?> T.success() = BPResult.Success<T>(this)
fun <T> bpTransaction(statement: Transaction.() -> T) = try {
    transaction(statement = statement).success()
} catch (ex: Exception) {
    "${ex.message}".fail<T>()
}

