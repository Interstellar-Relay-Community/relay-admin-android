package flights.interstellar.admin.features.connectedInstance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.lifecycleScope
import flights.interstellar.admin.api.pojo.AuthorityConfig
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
                itemsState = viewModel.connectedInstanceListStateFlow.collectAsState(),
                onItemClickListener = {
                    if (viewModel.interstellarMode)
                        viewModel.authorityConfigDialogState.value = it to it.authorityConfig
                },
                authorityConfigDialogDisplayState = viewModel.authorityConfigDialogState,
                authorityConfigConfirmCallback = { item, authorityConfig ->
                    saveAuthorityConfig(item, authorityConfig)
                    viewModel.authorityConfigDialogState.value = null

                    refresh()
                },
                authorityConfigDismissCallback = {
                    viewModel.authorityConfigDialogState.value = null

                    refresh()
                }
            )
        }

        lifecycleScope.launch {
            viewModel.initialise(dataStore = dataStore)
            refresh()
        }
    }

    private suspend fun refresh() {
        viewModel.initialise(dataStore)
        viewModel.invalidateViewModel(dataStore.getApiKey())
    }

    private suspend fun saveAuthorityConfig(
        item: ConnectedInstanceItem,
        authorityConfig: AuthorityConfig?
    ) {
        viewModel.saveAuthorityConfig(dataStore.getApiKey(), item, authorityConfig)
    }
}