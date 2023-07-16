package flights.interstellar.admin.features.connectedInstance

import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import flights.interstellar.admin.api.client.aodeClient
import flights.interstellar.admin.api.pojo.AuthorityConfig
import flights.interstellar.admin.common.PREFKEY_INSTANCEAPIKEY
import flights.interstellar.admin.common.PREFKEY_INTERSTELLARMODE
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.net.URL
import kotlin.properties.Delegates

class ConnectedInstanceViewModel : ViewModel() {
    val connectedInstanceListStateFlow = MutableStateFlow<List<ConnectedInstanceItem>?>(null)

    val snackbarHostState = SnackbarHostState()

    private lateinit var apiBaseUrl: String
    private var _interstellarMode by Delegates.notNull<Boolean>()
    val interstellarMode
        get() = _interstellarMode

    private val viewModelRefreshMutex = Mutex()

    val authorityConfigDialogState: MutableState<Pair<ConnectedInstanceItem, AuthorityConfig?>?> =
        mutableStateOf(null)

    suspend fun initialise(dataStore: DataStore<Preferences>) {
        apiBaseUrl = dataStore.data.map {
            it[PREFKEY_INSTANCEAPIKEY]
        }.first().orEmpty()

        _interstellarMode = dataStore.data.map {
            it[PREFKEY_INTERSTELLARMODE]
        }.first() ?: false
    }

    suspend fun invalidateViewModel(token: String) {
        viewModelRefreshMutex.withLock {
            // First empty the list...
            connectedInstanceListStateFlow.value = null

            // TODO: Implement pseudo-AP client to fetch instance's name and everything...
            try {
                val authorityConfigList = when (interstellarMode) {
                    true -> {
                        aodeClient.getAuthorityConfig(
                            token = token,
                            apiBaseUrl = apiBaseUrl
                        )
                    }
                    false -> mapOf()
                }

                connectedInstanceListStateFlow.value =
                    aodeClient.getConnected(token = token, apiBaseUrl = apiBaseUrl).map { actor ->
                        val instanceUrl = URL(actor).host

                        ConnectedInstanceItem(
                            instanceUrl = instanceUrl,
                            authorityConfig = authorityConfigList[instanceUrl]
                        )
                    }.sortedBy { it.instanceUrl }


            } catch (e: Exception) {
                Log.v("ConnectedInstanceViewModel", e.stackTraceToString())
                snackbarHostState.showSnackbar(message = e.stackTraceToString().take(50))
            }
        }
    }

    suspend fun saveAuthorityConfig(
        token: String,
        item: ConnectedInstanceItem,
        authorityConfig: AuthorityConfig?
    ) {
        when (authorityConfig == null) {
            true -> aodeClient.deleteAuthorityConfig(
                token = token,
                apiBaseUrl = apiBaseUrl,
                domain = item.instanceUrl
            )
            false -> aodeClient.putAuthorityConfig(
                token = token,
                apiBaseUrl = apiBaseUrl,
                domain = item.instanceUrl,
                authorityConfig = authorityConfig
            )
        }

    }
}