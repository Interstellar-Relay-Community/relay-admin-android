package flights.interstellar.admin.features.settings

import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import flights.interstellar.admin.common.PREFKEY_APIKEY
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class SettingViewModel : ViewModel() {
    val apiKeyState = mutableStateOf("")

    suspend fun loadViewModel(dataStore: DataStore<Preferences>) {
        apiKeyState.value = dataStore.data.map {
            it[PREFKEY_APIKEY]
        }.first().orEmpty()
    }

    suspend fun saveViewModel(dataStore: DataStore<Preferences>) {
        dataStore.edit {
            it[PREFKEY_APIKEY] = apiKeyState.value
        }
    }
}