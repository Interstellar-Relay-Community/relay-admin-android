package flights.interstellar.admin.features.allowedInstance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.lifecycleScope
import flights.interstellar.admin.common.dataStore
import flights.interstellar.admin.common.getApiKey
import kotlinx.coroutines.launch

class AllowedInstanceActivity : ComponentActivity() {

    private val viewModel: AllowedInstanceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MainScreen(
                addButtonCallback = {
                    viewModel.addAllowedInstanceDialogOpenState.value = true
                },
                editButtonCallback = {
                    viewModel.editMode.value = !viewModel.editMode.value
                },
                itemDeleteRequestedCallback = {
                    deleteAndUpdateList(it)
                },
                refreshButtonCallback = {
                    updateList()
                },
                backButtonCallback = { finish() },
                snackbarState = viewModel.snackbarState,
                itemsState = viewModel.connectedInstanceListStateFlow.collectAsState(),
                editModeState = viewModel.editMode.collectAsState()
            )

            InstanceAddDialog(
                dialogOpenState = viewModel.addAllowedInstanceDialogOpenState.collectAsState(),
                dialogTextFieldState = viewModel.addAllowedInstanceDialogTextFlow.collectAsState(),
                dialogTextFieldChangeCallback = {
                    viewModel.addAllowedInstanceDialogTextFlow.value = it
                },
                dialogDismissRequestCallback = {
                    closeDialog()
                    flushInstanceDialogContent()
                },
                dialogConfirmCallback = {
                    closeDialog()
                    addAndUpdateList()
                    flushInstanceDialogContent()
                }
            )
        }

        lifecycleScope.launch {
            viewModel.initialise(dataStore)
            updateList()
        }
    }

    private suspend fun addAndUpdateList() {
        viewModel.addAllowedInstance(
            dataStore.getApiKey(),
            viewModel.addAllowedInstanceDialogTextFlow.value
        )

        updateList()
    }

    private fun closeDialog() {
        viewModel.addAllowedInstanceDialogOpenState.value =
            !viewModel.addAllowedInstanceDialogOpenState.value
    }

    private fun flushInstanceDialogContent() {
        viewModel.addAllowedInstanceDialogTextFlow.value = ""
    }

    private suspend fun deleteAndUpdateList(it: AllowedInstanceItem) {
        viewModel.deleteAllowedInstance(dataStore.getApiKey(), it)
        updateList()
    }

    private suspend fun updateList() {
        viewModel.loadAllowedInstance(dataStore.getApiKey())
    }
}