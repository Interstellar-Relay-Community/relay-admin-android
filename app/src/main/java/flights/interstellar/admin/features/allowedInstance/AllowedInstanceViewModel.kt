package flights.interstellar.admin.features.allowedInstance

import androidx.compose.material3.SnackbarHostState
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

class AllowedInstanceViewModel : ViewModel() {
    val connectedInstanceListStateFlow = MutableStateFlow<List<AllowedInstanceItem>?>(null)
    val editMode = MutableStateFlow(false)

    val addAllowedInstanceDialogOpenState = MutableStateFlow(false)
    val addAllowedInstanceDialogTextFlow = MutableStateFlow("")

    val snackbarState = SnackbarHostState()

    private lateinit var apiBaseUrl: String
    private val viewModelMutex = Mutex()

    suspend fun initialise(dataStore: DataStore<Preferences>) {
        apiBaseUrl = dataStore.data.map {
            it[PREFKEY_INSTANCEAPIKEY]
        }.first().orEmpty()
    }

    suspend fun loadAllowedInstance(token: String) {
        viewModelMutex.withLock {
            // First empty the list...
            connectedInstanceListStateFlow.value = null

            // TODO: Implement pseudo-AP client to fetch instance's name and everything...
            try {
                connectedInstanceListStateFlow.value =
                    aodeClient.getAllowed(token = token, apiBaseUrl = apiBaseUrl)
                        .map { instanceUrl ->
                            AllowedInstanceItem(
                                instanceUrl = instanceUrl,
                            )
                        }.sortedBy { it.instanceUrl }
            } catch (e: Exception) {
                snackbarState.showSnackbar(message = e.stackTraceToString().take(50))
            }
        }
    }

    suspend fun addAllowedInstance(token: String, instanceUrl: String) {
        viewModelMutex.withLock {
            try {
                aodeClient.postAllow(
                    token = token,
                    apiBaseUrl = apiBaseUrl,
                    allowList = listOf(instanceUrl)
                )
            } catch (e: Exception) {
                snackbarState.showSnackbar(message = e.stackTraceToString().take(50))
            }
        }
    }

    suspend fun deleteAllowedInstance(token: String, allowedInstanceItem: AllowedInstanceItem) {
        viewModelMutex.withLock {
            try {
                aodeClient.postDisallow(
                    token = token,
                    apiBaseUrl = apiBaseUrl,
                    disallowList = listOf(allowedInstanceItem.instanceUrl)
                )
            } catch (e: Exception) {
                snackbarState.showSnackbar(message = e.stackTraceToString().take(50))
            }
        }
    }
}