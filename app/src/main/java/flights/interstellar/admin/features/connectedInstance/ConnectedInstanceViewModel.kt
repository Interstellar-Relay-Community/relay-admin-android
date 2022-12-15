package flights.interstellar.admin.features.connectedInstance

import androidx.lifecycle.ViewModel
import flights.interstellar.admin.api.client.aodeClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.net.URL

class ConnectedInstanceViewModel : ViewModel() {
    val connectedInstanceListStateFlow = MutableStateFlow<List<ConnectedInstanceItem>?>(null)

    private val viewModelRefreshMutex = Mutex()

    suspend fun invalidateViewModel(token: String) {
        viewModelRefreshMutex.withLock {
            // First empty the list...
            connectedInstanceListStateFlow.value = null

            // TODO: Implement pseudo-AP client to fetch instance's name and everything...
            connectedInstanceListStateFlow.value =
                aodeClient.getConnected(token = token).map { actor ->
                    val instanceUrl = URL(actor).host

                    ConnectedInstanceItem(
                        instanceUrl = instanceUrl
                    )
                }.sortedBy { it.instanceUrl }
        }
    }
}