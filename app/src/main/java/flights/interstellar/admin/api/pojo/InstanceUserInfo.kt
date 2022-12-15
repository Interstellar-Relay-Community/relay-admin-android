package flights.interstellar.admin.api.pojo

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.JsonNames

@OptIn(ExperimentalSerializationApi::class)
@kotlinx.serialization.Serializable
data class InstanceUserInfo(
    @JsonNames("username")
    val handle: String,
    @JsonNames("acct")
    val fullHandle: String,
    @JsonNames("display_name")
    val displayName: String,
    @JsonNames("avatar")
    val profilePhotoUrl: String
)