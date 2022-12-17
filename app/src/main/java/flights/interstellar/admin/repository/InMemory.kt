package flights.interstellar.admin.repository

import android.util.Log
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.*

/**
 * Simple memory based cache, backed with map.
 */
class InMemory<K, V> : KVInterface<K, V> {
    private val backingMap = mutableMapOf<K, ExpiryWrapper<V>>()
    private val mapAccessMutex = Mutex()

    override suspend fun get(key: K): V? {
        return mapAccessMutex.withLock {
            if (!backingMap.containsKey(key)) {
                Log.v("InMemory", "Get(key: $key) - No key")
                return@withLock null
            }

            // Check expiry
            if (isKeyExpired(key)) {
                Log.v("InMemory", "Get(key: $key) - Expired")
                return@withLock null
            }

            backingMap[key]!!.value
        }
    }

    override suspend fun has(key: K): Boolean {
        return get(key) != null
    }

    override suspend fun set(key: K, value: V, expiryDurationMs: Long, nowEpoch: Long) {
        return mapAccessMutex.withLock {
            Log.v("InMemory", "Set(key: $key, value: $value)")
            backingMap[key] =
                ExpiryWrapper(value = value, expiryTimeEpoch = nowEpoch + expiryDurationMs)
        }
    }

    private fun isKeyExpired(
        key: K,
        nowEpoch: Long = Calendar.getInstance().timeInMillis
    ): Boolean {
        Log.v("InMemory", "IsKeyExpired(key: $key")
        return backingMap[key]!!.expiryTimeEpoch < nowEpoch
    }

    companion object {
        private data class ExpiryWrapper<V>(
            val value: V,
            val expiryTimeEpoch: Long
        )
    }
}