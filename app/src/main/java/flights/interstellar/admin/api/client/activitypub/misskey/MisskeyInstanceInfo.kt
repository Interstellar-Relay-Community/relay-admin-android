package flights.interstellar.admin.api.client.activitypub.misskey

import flights.interstellar.admin.api.pojo.InstanceInfo
import flights.interstellar.admin.api.pojo.InstanceUserInfo

@kotlinx.serialization.Serializable
data class MisskeyInstanceInfo(
    val name: String?,
    val description: String?,
    val maintainerName: String?,
    val iconUrl: String?
) {
    fun toInstanceInfo(): InstanceInfo {
        return InstanceInfo(
            instanceName = name,
            instanceDescription = description, //TODO: Parse this to plain text? Or make app display (hopefully sanitised) HTML in the future?
            instanceAdminUser = InstanceUserInfo(
                profilePhotoUrl = iconUrl // Yes... This is not a admin user's profile photo...
            )
        )
    }
}
