package flights.interstellar.admin.features.connectedInstance

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import flights.interstellar.admin.api.client.aodeClient
import flights.interstellar.admin.common.PREFKEY_INSTANCEAPIKEY
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.net.URL

class ConnectedInstanceViewModel : ViewModel() {
    val connectedInstanceListStateFlow = MutableStateFlow<List<ConnectedInstanceItem>?>(null)

    private lateinit var apiBaseUrl: String
    private val viewModelRefreshMutex = Mutex()

    suspend fun initialise(dataStore: DataStore<Preferences>) {
        apiBaseUrl = dataStore.data.map {
            it[PREFKEY_INSTANCEAPIKEY]
        }.first().orEmpty()
    }

    suspend fun invalidateViewModel(token: String) {
        viewModelRefreshMutex.withLock {
            // First empty the list...
            connectedInstanceListStateFlow.value = null

            // TODO: Implement pseudo-AP client to fetch instance's name and everything...
            connectedInstanceListStateFlow.value =
                aodeClient.getConnected(token = token, apiBaseUrl = apiBaseUrl).map { actor ->
                    val instanceUrl = URL(actor).host

                    ConnectedInstanceItem(
                        instanceUrl = instanceUrl
                    )
                }.sortedBy { it.instanceUrl }
        }
    }
}