package flights.interstellar.admin.features.connectedInstance

import flights.interstellar.admin.api.pojo.AuthorityConfig
import flights.interstellar.admin.api.pojo.InstanceUrl

data class ConnectedInstanceItem(
    val instanceUrl: InstanceUrl,
    val authorityConfig: AuthorityConfig?
)