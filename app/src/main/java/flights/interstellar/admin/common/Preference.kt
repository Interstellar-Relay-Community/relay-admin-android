package flights.interstellar.admin.common

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
val PREFKEY_APIKEY = stringPreferencesKey("PREFKEY_APIKEY")

// TODO: Move this to helper in the near future

suspend fun DataStore<Preferences>.getApiKey(): String {
    return this.data.map {
        it[PREFKEY_APIKEY]
    }.first().orEmpty()
}