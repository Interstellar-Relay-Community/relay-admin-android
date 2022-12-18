package flights.interstellar.admin.api.client.activitypub.mastodon

import flights.interstellar.admin.api.pojo.InstanceInfo
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.JsonNames

@OptIn(ExperimentalSerializationApi::class)
@kotlinx.serialization.Serializable
data class MastodonInstanceInfo(
    @JsonNames("title")
    val title: String?,
    @JsonNames("short_description")
    val shortDescription: String?,
    @JsonNames("contact_account")
    val contactAccount: MastodonInstanceUserInfo?
) {
    fun toInstanceInfo(): InstanceInfo {
        return InstanceInfo(
            instanceName = title,
            instanceDescription = shortDescription,
            instanceAdminUser = contactAccount?.toInstanceUserInfo()
        )
    }
}

