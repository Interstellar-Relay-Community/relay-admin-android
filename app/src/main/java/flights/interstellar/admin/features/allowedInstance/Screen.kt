package flights.interstellar.admin.features.allowedInstance

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults.exitUntilCollapsedScrollBehavior
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.SecureFlagPolicy
import androidx.lifecycle.lifecycleScope
import coil.compose.AsyncImage
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import flights.interstellar.admin.R
import flights.interstellar.admin.api.pojo.InstanceInfo
import flights.interstellar.admin.common.InterstallarAdminTheme
import flights.interstellar.admin.common.Purple80
import flights.interstellar.admin.common.Typography
import flights.interstellar.admin.repository.instanceInfoRepository
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    backButtonCallback: suspend () -> Unit,
    editButtonCallback: suspend () -> Unit,
    addButtonCallback: suspend () -> Unit,
    refreshButtonCallback: suspend () -> Unit,
    itemDeleteRequestedCallback: suspend (AllowedInstanceItem) -> Unit,
    snackbarState: SnackbarHostState,
    itemsState: State<List<AllowedInstanceItem>?>,
    editModeState: State<Boolean>
) {
    val scrollBehaviour = exitUntilCollapsedScrollBehavior()
    val lifecycleScope = LocalLifecycleOwner.current.lifecycleScope

    InterstallarAdminTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                LargeTopAppBar(
                    title = {
                        Text(
                            text = stringResource(id = R.string.title_activity_allowedInstance)
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { lifecycleScope.launch { backButtonCallback.invoke() } }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    actions = {
                        when (editModeState.value) {
                            true -> {
                                IconButton(
                                    onClick = { lifecycleScope.launch { editButtonCallback.invoke() } }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Done,
                                        contentDescription = "Done"
                                    )
                                }
                            }
                            false -> {
                                IconButton(
                                    onClick = { lifecycleScope.launch { refreshButtonCallback.invoke() } }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Refresh"
                                    )
                                }
                                IconButton(
                                    onClick = { lifecycleScope.launch { editButtonCallback.invoke() } }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit"
                                    )
                                }
                                IconButton(
                                    onClick = { lifecycleScope.launch { addButtonCallback.invoke() } }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add"
                                    )
                                }
                            }
                        }
                    },
                    scrollBehavior = scrollBehaviour
                )
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarState)
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues = paddingValues)
            ) {
                itemsState.value?.size.let {
                    AnimatedVisibility(
                        visible = it != null,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        Text(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            text = stringResource(id = R.string.total_instances).format(
                                Locale.US,
                                it
                            )
                        )
                    }
                }
                LazyColumn(
                    modifier = Modifier
                        .nestedScroll(scrollBehaviour.nestedScrollConnection)
                ) {
                    itemsState.value?.let {
                        items(it) { item ->
                            AllowedInstanceListItem(
                                item = item,
                                editMode = editModeState.value,
                                deletePressedCallback = {
                                    lifecycleScope.launch {
                                        itemDeleteRequestedCallback.invoke(item)
                                    }
                                }
                            )
                        }
                    } ?: run {
                        item {
                            repeat(3) {
                                AllowedInstanceListSkeletonItem()
                            }
                        }
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen(
        addButtonCallback = {},
        editButtonCallback = {},
        backButtonCallback = {},
        refreshButtonCallback = {},
        itemDeleteRequestedCallback = {},
        itemsState = remember {
            mutableStateOf(
                listOf(
                    AllowedInstanceItem(instanceUrl = "example.com"),
                    AllowedInstanceItem(instanceUrl = "example2.com"),
                    AllowedInstanceItem(instanceUrl = "example3.com")
                )
            )
        },
        snackbarState = remember { SnackbarHostState() },
        editModeState = remember { mutableStateOf(false) }
    )
}

@Preview(showBackground = true)
@Composable
fun DialogPreview() {
    InstanceAddDialog(
        dialogOpenState = remember { mutableStateOf(true) },
        dialogTextFieldState = remember { mutableStateOf("") },
        dialogTextFieldChangeCallback = {},
        dialogDismissRequestCallback = {},
        dialogConfirmCallback = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllowedInstanceListItem(
    item: AllowedInstanceItem,
    editMode: Boolean,
    deletePressedCallback: () -> Unit
) {
    val apLoadingCompleteState = remember { mutableStateOf(false) }
    val instanceInfoState = remember { mutableStateOf<InstanceInfo?>(null) }

    Column {
        ListItem(
            headlineText = {
                Text(
                    text = instanceInfoState.value?.instanceName ?: item.instanceUrl,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            supportingText = {
                Text(
                    text = item.instanceUrl,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            leadingContent = {
                AsyncImage(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    model = instanceInfoState.value?.instanceAdminUser?.profilePhotoUrl,
                    contentDescription = stringResource(R.string.instance_admin_profile_photo)
                )
            },
            trailingContent = {
                if (editMode) {
                    IconButton(
                        onClick = deletePressedCallback,
                        content = {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = stringResource(R.string.delete)
                            )
                        }
                    )
                }
            }
        )
        Divider()
    }

    LaunchedEffect(Unit) {
        val instanceInfo = try {
            instanceInfoRepository.getInstanceInfo(item.instanceUrl)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

        instanceInfo?.let {
            instanceInfoState.value = it
        }

        apLoadingCompleteState.value = true
    }
}

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllowedInstanceListSkeletonItem() {
    Column {
        ListItem(
            headlineText = {
                Text(
                    modifier = Modifier
                        .padding(2.dp)
                        .placeholder(true, highlight = PlaceholderHighlight.fade()),
                    text = "Lorem ipsum",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            supportingText = {
                Text(
                    modifier = Modifier
                        .padding(2.dp)
                        .placeholder(true, highlight = PlaceholderHighlight.fade()),
                    text = "Ipsum sunt eos et quia ut sequi saepe.",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            leadingContent = {
                Image(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .placeholder(true, highlight = PlaceholderHighlight.fade()),
                    painter = ColorPainter(color = Purple80),
                    contentDescription = stringResource(R.string.instance_admin_profile_photo)
                )
            }
        )
        Divider()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstanceAddDialog(
    dialogOpenState: State<Boolean>,
    dialogTextFieldState: State<String>,
    dialogTextFieldChangeCallback: suspend (String) -> Unit,
    dialogDismissRequestCallback: suspend () -> Unit,
    dialogConfirmCallback: suspend () -> Unit
) {
    val lifecycleScope = LocalLifecycleOwner.current.lifecycleScope

    if (dialogOpenState.value) {
        Dialog(
            onDismissRequest = { lifecycleScope.launch { dialogDismissRequestCallback.invoke() } },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = false,
                securePolicy = SecureFlagPolicy.Inherit
            )
        ) {
            Surface(
                shape = RoundedCornerShape(28.dp),
                tonalElevation = 3.dp
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .defaultMinSize(minWidth = 280.dp)
                ) {
                    Text(
                        text = "Add allowed instance",
                        style = Typography.headlineSmall
                    )
                    Spacer(Modifier.height(16.dp))
                    TextField(
                        value = dialogTextFieldState.value,
                        onValueChange = {
                            lifecycleScope.launch {
                                dialogTextFieldChangeCallback.invoke(
                                    it
                                )
                            }
                        },
                        label = {
                            Text("Instance domain")
                        }
                    )
                    Spacer(Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.align(Alignment.End),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = { lifecycleScope.launch { dialogDismissRequestCallback.invoke() } }
                        ) {
                            Text("Cancel")
                        }
                        TextButton(
                            onClick = { lifecycleScope.launch { dialogConfirmCallback.invoke() } }
                        ) {
                            Text("Add")
                        }
                    }
                }
            }
        }
    }
}