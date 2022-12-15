package flights.interstellar.admin.api

import flights.interstellar.admin.api.pojo.Actor
import flights.interstellar.admin.api.pojo.AodeRelayStats
import flights.interstellar.admin.api.pojo.InstanceUrl

interface AodeRelayAdminApiInterface {
    suspend fun postAllow(token: String, allowList: List<InstanceUrl>)

    suspend fun postDisallow(token: String, disallowList: List<InstanceUrl>)

    suspend fun postBlock(token: String, blockList: List<InstanceUrl>)

    suspend fun postUnblock(token: String, unblockList: List<InstanceUrl>)

    suspend fun getAllowed(token: String): List<InstanceUrl>

    suspend fun getBlocked(token: String): List<InstanceUrl>

    suspend fun getConnected(token: String): List<Actor>

    suspend fun getStats(token: String): AodeRelayStats
}