package flights.interstellar.admin.api

import flights.interstellar.admin.api.pojo.Actor
import flights.interstellar.admin.api.pojo.AodeRelayStats
import flights.interstellar.admin.api.pojo.InstanceUrl

interface AodeRelayAdminApiInterface {
    suspend fun postAllow(token: String, apiBaseUrl: String, allowList: List<InstanceUrl>)

    suspend fun postDisallow(token: String, apiBaseUrl: String, disallowList: List<InstanceUrl>)

    suspend fun postBlock(token: String, apiBaseUrl: String, blockList: List<InstanceUrl>)

    suspend fun postUnblock(token: String, apiBaseUrl: String, unblockList: List<InstanceUrl>)

    suspend fun getAllowed(token: String, apiBaseUrl: String): List<InstanceUrl>

    suspend fun getBlocked(token: String, apiBaseUrl: String): List<InstanceUrl>

    suspend fun getConnected(token: String, apiBaseUrl: String): List<Actor>

    suspend fun getStats(token: String, apiBaseUrl: String): AodeRelayStats
}