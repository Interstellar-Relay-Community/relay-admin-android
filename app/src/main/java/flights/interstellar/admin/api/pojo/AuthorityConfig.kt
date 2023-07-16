package flights.interstellar.admin.api.pojo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthorityConfig(
    val probability: Int,
    @SerialName("enable_probability")
    val enableProbability: Boolean,
    @SerialName("authority_set")
    val authoritySet: List<String>,
    @SerialName("is_allowlist")
    val isAllowList: Boolean,
    @SerialName("receive_only")
    val receiveOnly: Boolean
)