package flights.interstellar.admin.repository

import java.util.*

interface KVInterface<K, V> {
    suspend fun get(key: K): V?
    suspend fun has(key: K): Boolean
    suspend fun set(
        key: K,
        value: V,
        expiryDurationMs: Long,
        nowEpoch: Long = Calendar.getInstance().timeInMillis
    )
}