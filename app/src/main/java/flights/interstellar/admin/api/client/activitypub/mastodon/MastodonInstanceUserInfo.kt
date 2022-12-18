package flights.interstellar.admin.api.client.activitypub.mastodon

import flights.interstellar.admin.api.pojo.InstanceUserInfo
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.JsonNames

@OptIn(ExperimentalSerializationApi::class)
@kotlinx.serialization.Serializable
data class MastodonInstanceUserInfo(
    val username: String?,
    val acct: String?,
    @JsonNames("display_name")
    val displayName: String?,
    val avatar: String?
) {
    fun toInstanceUserInfo(): InstanceUserInfo {
        return InstanceUserInfo(
            profilePhotoUrl = avatar
        )
    }
}