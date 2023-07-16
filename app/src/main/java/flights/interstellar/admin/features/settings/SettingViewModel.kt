package flights.interstellar.admin.features.settings

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import flights.interstellar.admin.R
import flights.interstellar.admin.common.PREFKEY_APIKEY
import flights.interstellar.admin.common.PREFKEY_INSTANCEAPIKEY
import flights.interstellar.admin.common.PREFKEY_INTERSTELLARMODE
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class SettingViewModel : ViewModel() {
    val endpointUrlState = mutableStateOf("")
    val endpointUrlErrorMessageIdState = derivedStateOf {
        if (!(endpointUrlState.value.startsWith("http://") || endpointUrlState.value.startsWith("https://"))) {
            R.string.activity_setting_urlformaterror_notsupportedproto
        } else
            null
    }
    val apiKeyState = mutableStateOf("")
    val interstellarMode = mutableStateOf(false)

    suspend fun loadViewModel(dataStore: DataStore<Preferences>) {
        endpointUrlState.value = dataStore.data.map {
            it[PREFKEY_INSTANCEAPIKEY]
        }.first().orEmpty()

        apiKeyState.value = dataStore.data.map {
            it[PREFKEY_APIKEY]
        }.first().orEmpty()

        interstellarMode.value = dataStore.data.map {
            it[PREFKEY_INTERSTELLARMODE]
        }.first() ?: false
    }

    suspend fun saveViewModel(dataStore: DataStore<Preferences>) {
        dataStore.edit {
            it[PREFKEY_INSTANCEAPIKEY] = endpointUrlState.value
            it[PREFKEY_APIKEY] = apiKeyState.value
            it[PREFKEY_INTERSTELLARMODE] = interstellarMode.value
        }
    }
}