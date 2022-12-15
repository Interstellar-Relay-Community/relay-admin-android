package flights.interstellar.admin.api.pojo

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class GetAllowedResponse(
    @JsonNames("allowed_domains")
    val allowedDomains: List<InstanceUrl>
)