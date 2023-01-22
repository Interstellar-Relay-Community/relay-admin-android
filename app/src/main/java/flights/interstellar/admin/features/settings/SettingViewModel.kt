package flights.interstellar.admin.features.settings

import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import flights.interstellar.admin.common.PREFKEY_APIKEY
import flights.interstellar.admin.common.PREFKEY_INSTANCEAPIKEY
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class SettingViewModel : ViewModel() {
    val endpointUrlState = mutableStateOf("")
    val apiKeyState = mutableStateOf("")

    suspend fun loadViewModel(dataStore: DataStore<Preferences>) {
        endpointUrlState.value = dataStore.data.map {
            it[PREFKEY_INSTANCEAPIKEY]
        }.first().orEmpty()

        apiKeyState.value = dataStore.data.map {
            it[PREFKEY_APIKEY]
        }.first().orEmpty()
    }

    suspend fun saveViewModel(dataStore: DataStore<Preferences>) {
        dataStore.edit {
            it[PREFKEY_INSTANCEAPIKEY] = endpointUrlState.value
            it[PREFKEY_APIKEY] = apiKeyState.value
        }
    }
}