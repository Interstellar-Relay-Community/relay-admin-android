package flights.interstellar.admin.api.client

import flights.interstellar.admin.api.AodeRelayAdminApiInterface
import flights.interstellar.admin.api.pojo.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

//TODO: Handle global API exception cases
val aodeClient = AodeClient()
val MEDIATYPE_JSON = "application/json".toMediaType()

@OptIn(ExperimentalSerializationApi::class)
@Suppress("BlockingMethodInNonBlockingContext")
class AodeClient : AodeRelayAdminApiInterface {
    private val apiBaseUrl = "https://ap.interstellar.flights"
    private val path = "api/v1/admin"

    override suspend fun postAllow(token: String, allowList: List<InstanceUrl>) {
        val request = createRequest(
            token = token,
            endpointName = "allow",
            body = Json.encodeToString(PostAllowRequest(allowList))
                .toRequestBody(contentType = MEDIATYPE_JSON)
        )

        return withContext(Dispatchers.IO) {
            okHttpClient.newCall(request).execute()
        }
    }

    override suspend fun postDisallow(token: String, disallowList: List<InstanceUrl>) {
        val request = createRequest(
            token = token,
            endpointName = "disallow",
            body = Json.encodeToString(PostAllowRequest(disallowList))
                .toRequestBody(contentType = MEDIATYPE_JSON)
        )

        return withContext(Dispatchers.IO) {
            okHttpClient.newCall(request).execute()
        }
    }

    override suspend fun postBlock(token: String, blockList: List<InstanceUrl>) {
        TODO("Not yet implemented")
    }

    override suspend fun postUnblock(token: String, unblockList: List<InstanceUrl>) {
        TODO("Not yet implemented")
    }

    override suspend fun getAllowed(token: String): List<InstanceUrl> {
        val request = createRequest(token = token, endpointName = "allowed")

        return withContext(Dispatchers.IO) {
            okHttpClient.newCall(request).execute().use {
                Json.decodeFromStream<GetAllowedResponse>(it.body!!.byteStream()).allowedDomains
            }
        }
    }

    override suspend fun getBlocked(token: String): List<InstanceUrl> {
        TODO("Not yet implemented")
    }

    override suspend fun getConnected(token: String): List<Actor> {
        val request = createRequest(token = token, endpointName = "connected")

        return withContext(Dispatchers.IO) {
            okHttpClient.newCall(request).execute().use {
                Json.decodeFromStream<GetConnectedResponse>(it.body!!.byteStream()).connectedActors
            }
        }
    }

    override suspend fun getStats(token: String): AodeRelayStats {
        TODO("Not yet implemented")
    }

    private fun createRequest(
        body: RequestBody? = null,
        token: String,
        endpointName: String
    ): Request {
        return Request.Builder()
            .run {
                body?.let {
                    post(it)
                } ?: get()
            }
            .url("$apiBaseUrl/$path/$endpointName")
            .addHeader("X-API-TOKEN", token)
            .build()
    }
}