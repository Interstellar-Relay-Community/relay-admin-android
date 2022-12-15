package flights.interstellar.admin.api.client

import flights.interstellar.admin.api.ApClientInterface
import flights.interstellar.admin.api.pojo.InstanceInfo
import flights.interstellar.admin.api.pojo.InstanceUserInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import okhttp3.Request

val apClient = ApClient()

//TODO: Support non-mastodon cases by utilising webfinger instead of using instance API to fetch admin user
@OptIn(ExperimentalSerializationApi::class)
class ApClient : ApClientInterface {
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun getUserInfo(instanceBaseUrl: String, handle: String): InstanceUserInfo {
        //TODO: Refactor createRequest to properly handle query params
        val request = createRequest(
            apiBaseUrl = instanceBaseUrl,
            endpointName = "accounts/lookup?acct=$handle"
        )

        return withContext(Dispatchers.IO) {
            okHttpClient.newCall(request).execute().use {
                json.decodeFromStream(it.body!!.byteStream())
            }
        }
    }

    override suspend fun getInstanceInfo(instanceBaseUrl: String): InstanceInfo {
        val request =
            createRequest(apiBaseUrl = instanceBaseUrl, path = "api/v1", endpointName = "instance")

        return withContext(Dispatchers.IO) {
            okHttpClient.newCall(request).execute().use {
                json.decodeFromStream(it.body!!.byteStream())
            }
        }
    }
}

private fun createRequest(
    apiBaseUrl: String,
    path: String = "api/v1",
    endpointName: String
): Request {
    return Request.Builder()
        .get()
        .url("https://$apiBaseUrl/$path/$endpointName")
        .build()
}