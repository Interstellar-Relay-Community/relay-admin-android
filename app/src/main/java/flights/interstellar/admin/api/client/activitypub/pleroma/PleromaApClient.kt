package flights.interstellar.admin.api.client.activitypub.pleroma

import flights.interstellar.admin.api.ApClientInterface
import flights.interstellar.admin.api.client.okHttpClient
import flights.interstellar.admin.api.pojo.InstanceInfo
import flights.interstellar.admin.api.pojo.InstanceUrl
import flights.interstellar.admin.api.pojo.InstanceUserInfo
import flights.interstellar.admin.api.pojo.UserHandle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import okhttp3.Request

@OptIn(ExperimentalSerializationApi::class)
class PleromaApClient : ApClientInterface {
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun getUserInfo(
        instanceBaseUrl: InstanceUrl,
        handle: UserHandle
    ): InstanceUserInfo {
        TODO("Not yet implemented")
    }

    override suspend fun getInstanceInfo(instanceBaseUrl: InstanceUrl): InstanceInfo {
        val request =
            createRequest(apiBaseUrl = instanceBaseUrl, endpointName = "instance")

        return withContext(Dispatchers.IO) {
            okHttpClient.newCall(request).execute().use {
                json.decodeFromStream<PleromaInstanceInfo>(it.body!!.byteStream())
            }
        }.toInstanceInfo()
    }

    private fun createRequest(
        apiBaseUrl: InstanceUrl,
        path: String = "api/v1",
        endpointName: String
    ): Request {
        return Request.Builder()
            .get()
            .url("https://$apiBaseUrl/$path/$endpointName")
            .build()
    }
}