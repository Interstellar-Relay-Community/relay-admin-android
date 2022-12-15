package flights.interstellar.admin.api

import flights.interstellar.admin.api.pojo.InstanceInfo
import flights.interstellar.admin.api.pojo.InstanceUserInfo

interface ApClientInterface {
    suspend fun getUserInfo(instanceBaseUrl: String, handle: String): InstanceUserInfo
    suspend fun getInstanceInfo(instanceBaseUrl: String): InstanceInfo
}