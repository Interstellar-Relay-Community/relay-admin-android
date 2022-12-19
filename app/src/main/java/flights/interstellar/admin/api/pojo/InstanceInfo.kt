package flights.interstellar.admin.api.pojo

@kotlinx.serialization.Serializable
data class InstanceInfo(
    val instanceName: String?,
    val instanceDescription: String?,
    val instanceAdminUser: InstanceUserInfo?
)