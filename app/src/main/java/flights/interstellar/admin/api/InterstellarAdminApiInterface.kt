package flights.interstellar.admin.api

import flights.interstellar.admin.api.pojo.AuthorityConfig

interface InterstellarAdminApiInterface {
    suspend fun getAuthorityConfig(token: String, apiBaseUrl: String): Map<String, AuthorityConfig>

    suspend fun putAuthorityConfig(
        token: String,
        apiBaseUrl: String,
        domain: String,
        authorityConfig: AuthorityConfig
    )

    suspend fun deleteAuthorityConfig(token: String, apiBaseUrl: String, domain: String)
}