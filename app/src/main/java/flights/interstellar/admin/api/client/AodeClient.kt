package flights.interstellar.admin.api.client

import flights.interstellar.admin.api.AodeRelayAdminApiInterface
import flights.interstellar.admin.api.InterstellarAdminApiInterface
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
class AodeClient : AodeRelayAdminApiInterface, InterstellarAdminApiInterface {
    private val path = "api/v1/admin"

    override suspend fun postAllow(
        token: String,
        apiBaseUrl: String,
        allowList: List<InstanceUrl>
    ) {
        val request = createRequest(
            token = token,
            apiBaseUrl = apiBaseUrl,
            endpointName = "allow",
            body = Json.encodeToString(PostAllowRequest(allowList))
                .toRequestBody(contentType = MEDIATYPE_JSON),
            method = "POST"
        )

        return withContext(Dispatchers.IO) {
            okHttpClient.newCall(request).execute()
        }
    }

    override suspend fun postDisallow(
        token: String,
        apiBaseUrl: String,
        disallowList: List<InstanceUrl>
    ) {
        val request = createRequest(
            token = token,
            apiBaseUrl = apiBaseUrl,
            endpointName = "disallow",
            body = Json.encodeToString(PostAllowRequest(disallowList))
                .toRequestBody(contentType = MEDIATYPE_JSON),
            method = "POST"
        )

        return withContext(Dispatchers.IO) {
            okHttpClient.newCall(request).execute()
        }
    }

    override suspend fun postBlock(
        token: String,
        apiBaseUrl: String,
        blockList: List<InstanceUrl>
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun postUnblock(
        token: String,
        apiBaseUrl: String,
        unblockList: List<InstanceUrl>
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun getAllowed(token: String, apiBaseUrl: String): List<InstanceUrl> {
        val request =
            createRequest(
                token = token,
                method = "GET",
                apiBaseUrl = apiBaseUrl,
                endpointName = "allowed"
            )

        return withContext(Dispatchers.IO) {
            okHttpClient.newCall(request).execute().use {
                Json.decodeFromStream<GetAllowedResponse>(it.body!!.byteStream()).allowedDomains
            }
        }
    }

    override suspend fun getBlocked(token: String, apiBaseUrl: String): List<InstanceUrl> {
        TODO("Not yet implemented")
    }

    override suspend fun getConnected(token: String, apiBaseUrl: String): List<Actor> {
        val request =
            createRequest(
                token = token,
                method = "GET",
                apiBaseUrl = apiBaseUrl,
                endpointName = "connected"
            )

        return withContext(Dispatchers.IO) {
            okHttpClient.newCall(request).execute().use {
                Json.decodeFromStream<GetConnectedResponse>(it.body!!.byteStream()).connectedActors
            }
        }
    }

    override suspend fun getStats(token: String, apiBaseUrl: String): AodeRelayStats {
        TODO("Not yet implemented")
    }

    override suspend fun getAuthorityConfig(
        token: String,
        apiBaseUrl: String
    ): Map<String, AuthorityConfig> {
        val request =
            createRequest(
                token = token,
                method = "GET",
                apiBaseUrl = apiBaseUrl,
                endpointName = "authority_cfg/"
            )

        return withContext(Dispatchers.IO) {
            okHttpClient.newCall(request).execute().use {
                Json.decodeFromStream(it.body!!.byteStream())
            }
        }
    }

    override suspend fun putAuthorityConfig(
        token: String,
        apiBaseUrl: String,
        domain: String,
        authorityConfig: AuthorityConfig
    ) {
        val request = createRequest(
            token = token,
            apiBaseUrl = apiBaseUrl,
            endpointName = "authority_cfg/$domain",
            body = Json.encodeToString(authorityConfig)
                .toRequestBody(contentType = MEDIATYPE_JSON),
            method = "PUT"
        )

        return withContext(Dispatchers.IO) {
            okHttpClient.newCall(request).execute()
        }
    }

    override suspend fun deleteAuthorityConfig(token: String, apiBaseUrl: String, domain: String) {
        val request = createRequest(
            token = token,
            apiBaseUrl = apiBaseUrl,
            endpointName = "authority_cfg/$domain",
            method = "DELETE"
        )

        return withContext(Dispatchers.IO) {
            okHttpClient.newCall(request).execute()
        }
    }


    private fun createRequest(
        body: RequestBody? = null,
        method: String,
        token: String,
        endpointName: String,
        apiBaseUrl: String
    ): Request {
        return Request.Builder()
            .run {
                method(method, body)
            }
            .url("$apiBaseUrl/$path/$endpointName")
            .addHeader("X-API-TOKEN", token)
            .build()
    }
}