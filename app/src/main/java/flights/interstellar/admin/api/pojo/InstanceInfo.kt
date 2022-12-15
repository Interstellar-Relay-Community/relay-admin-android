package flights.interstellar.admin.api.pojo

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.JsonNames

@OptIn(ExperimentalSerializationApi::class)
@kotlinx.serialization.Serializable
data class InstanceInfo(
    @JsonNames("title")
    val instanceName: String,
    @JsonNames("short_description")
    val instanceDescription: String,
    @JsonNames("contact_account")
    val instanceAdminUser: InstanceUserInfo
)

@OptIn(ExperimentalSerializationApi::class)
@kotlinx.serialization.Serializable
data class InstanceContact(
    val email: String,
    @JsonNames("account")
    val instanceAdminUser: InstanceUserInfo
)