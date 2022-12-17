package flights.interstellar.admin.repository

import flights.interstellar.admin.api.client.apClient
import flights.interstellar.admin.api.pojo.InstanceInfo
import flights.interstellar.admin.api.pojo.InstanceUrl
import java.util.concurrent.TimeUnit

val instanceInfoRepository = InstanceInfoRepository()

class InstanceInfoRepository(private val globalExpiryMs: Long = TimeUnit.DAYS.toMillis(7)) {
    private val inMemoryCache = InMemory<InstanceUrl, InstanceInfo?>()

    suspend fun getInstanceInfo(instanceBaseUrl: InstanceUrl): InstanceInfo {
        if (!inMemoryCache.has(instanceBaseUrl)) {
            val instanceInfo = try {
                apClient.getInstanceInfo(instanceBaseUrl)
            } catch (_: Exception) {
                null
            }

            instanceInfo?.let {
                inMemoryCache.set(
                    key = instanceBaseUrl,
                    value = it,
                    expiryDurationMs = globalExpiryMs
                )

                it
            } ?: run {
                inMemoryCache.set(
                    key = instanceBaseUrl,
                    value = null,
                    expiryDurationMs = globalExpiryMs
                )
            }
        }

        return inMemoryCache.get(instanceBaseUrl)
            ?: throw RuntimeException("Simulated API response error")
    }
}