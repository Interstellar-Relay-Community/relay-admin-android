package flights.interstellar.admin.api.client.activitypub.pleroma

import flights.interstellar.admin.api.pojo.InstanceInfo
import flights.interstellar.admin.api.pojo.InstanceUserInfo

@kotlinx.serialization.Serializable
data class PleromaInstanceInfo(
    val title: String?,
    val description: String?,
    val thumbnail: String?
) {
    fun toInstanceInfo(): InstanceInfo {
        return InstanceInfo(
            instanceName = title,
            instanceDescription = description,
            instanceAdminUser = InstanceUserInfo(
                profilePhotoUrl = thumbnail
            )
        )
    }
}