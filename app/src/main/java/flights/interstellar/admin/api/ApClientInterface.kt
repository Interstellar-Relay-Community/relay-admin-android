package flights.interstellar.admin.api

import flights.interstellar.admin.api.pojo.InstanceInfo
import flights.interstellar.admin.api.pojo.InstanceUrl
import flights.interstellar.admin.api.pojo.InstanceUserInfo
import flights.interstellar.admin.api.pojo.UserHandle

interface ApClientInterface {
    suspend fun getUserInfo(instanceBaseUrl: InstanceUrl, handle: UserHandle): InstanceUserInfo
    suspend fun getInstanceInfo(instanceBaseUrl: InstanceUrl): InstanceInfo
}