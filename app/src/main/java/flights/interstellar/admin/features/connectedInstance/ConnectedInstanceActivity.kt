package flights.interstellar.admin.features.connectedInstance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.lifecycleScope
import flights.interstellar.admin.common.dataStore
import flights.interstellar.admin.common.getApiKey
import kotlinx.coroutines.launch

class ConnectedInstanceActivity : ComponentActivity() {

    private val viewModel: ConnectedInstanceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MainScreen(
                refreshButtonCallback = { refresh() },
                backButtonCallback = { finish() },
                snackbarState = viewModel.snackbarHostState,
                itemsState = viewModel.connectedInstanceListStateFlow.collectAsState()
            )
        }

        lifecycleScope.launch {
            viewModel.initialise(dataStore = dataStore)
            refresh()
        }
    }

    private suspend fun refresh() {
        lifecycleScope.launch {
            viewModel.initialise(dataStore)
            viewModel.invalidateViewModel(dataStore.getApiKey())
        }
    }
}