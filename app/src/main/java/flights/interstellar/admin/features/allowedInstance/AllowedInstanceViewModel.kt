package flights.interstellar.admin.features.allowedInstance

import androidx.lifecycle.ViewModel
import flights.interstellar.admin.api.client.aodeClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class AllowedInstanceViewModel : ViewModel() {
    val connectedInstanceListStateFlow = MutableStateFlow<List<AllowedInstanceItem>?>(null)
    val editMode = MutableStateFlow(false)

    val addAllowedInstanceDialogOpenState = MutableStateFlow(false)
    val addAllowedInstanceDialogTextFlow = MutableStateFlow("")

    private val viewModelMutex = Mutex()

    suspend fun loadAllowedInstance(token: String) {
        viewModelMutex.withLock {
            // First empty the list...
            connectedInstanceListStateFlow.value = null

            // TODO: Implement pseudo-AP client to fetch instance's name and everything...
            connectedInstanceListStateFlow.value =
                aodeClient.getAllowed(token = token).map { instanceUrl ->
                    AllowedInstanceItem(
                        instanceUrl = instanceUrl,
                    )
                }.sortedBy { it.instanceUrl }
        }
    }

    suspend fun addAllowedInstance(token: String, instanceUrl: String) {
        viewModelMutex.withLock {
            aodeClient.postAllow(token = token, allowList = listOf(instanceUrl))
        }
    }

    suspend fun deleteAllowedInstance(token: String, allowedInstanceItem: AllowedInstanceItem) {
        viewModelMutex.withLock {
            aodeClient.postDisallow(
                token = token,
                disallowList = listOf(allowedInstanceItem.instanceUrl)
            )
        }
    }
}