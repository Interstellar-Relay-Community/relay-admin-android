package flights.interstellar.admin.features.settings

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import flights.interstellar.admin.common.dataStore
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex

class SettingActivity : ComponentActivity() {

    private val viewModel: SettingViewModel by viewModels()
    private val persistMutex = Mutex()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen(
                endpointUrlValueState = viewModel.endpointUrlState,
                endpointUrlErrorMessageIdState = viewModel.endpointUrlErrorMessageIdState,
                endpointUrlChangeCallback = { viewModel.endpointUrlState.value = it },
                apiKeyValueState = viewModel.apiKeyState,
                apiKeyChangeCallback = { viewModel.apiKeyState.value = it },
                interstellarModeValueState = viewModel.interstellarMode,
                interstellarModeChangeCallback = { viewModel.interstellarMode.value = it },
                backButtonCallback = {
                    lifecycleScope.launch {
                        if (viewModel.endpointUrlErrorMessageIdState.value == null) {
                            handleDone()
                            finish()
                        }
                    }
                }
            )
        }

        lifecycleScope.launch {
            viewModel.loadViewModel(dataStore = dataStore)
        }
    }

    private suspend fun handleDone() {
        if (persistMutex.tryLock()) {
            viewModel.saveViewModel(dataStore = dataStore)
            persistMutex.unlock()
        }
    }
}