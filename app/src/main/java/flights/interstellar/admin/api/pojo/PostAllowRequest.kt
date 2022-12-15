package flights.interstellar.admin.api.pojo

@kotlinx.serialization.Serializable
data class PostAllowRequest(
    val domains: List<String>
)