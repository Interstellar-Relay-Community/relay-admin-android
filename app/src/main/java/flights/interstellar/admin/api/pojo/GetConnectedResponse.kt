package flights.interstellar.admin.api.pojo

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class GetConnectedResponse(
    @JsonNames("connected_actors")
    val connectedActors: List<Actor>
)