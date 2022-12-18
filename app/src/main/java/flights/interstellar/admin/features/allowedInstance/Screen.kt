package flights.interstellar.admin.features.allowedInstance

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.SecureFlagPolicy
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    backButtonCallback: () -> Unit,
    editButtonCallback: () -> Unit,
    addButtonCallback: () -> Unit,
    refreshButtonCallback: () -> Unit,
    itemDeleteRequestedCallback: (AllowedInstanceItem) -> Unit,
    itemsState: State<List<AllowedInstanceItem>?>,
    editModeState: State<Boolean>
) {
    val scrollBehaviour = exitUntilCollapsedScrollBehavior()

    InterstallarAdminTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            Column {
                LargeTopAppBar(
                    title = {
                        Text(
                            text = stringResource(id = R.string.title_activity_allowedInstance)
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = backButtonCallback
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
                                    onClick = editButtonCallback
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Done,
                                        contentDescription = "Done"
                                    )
                                }
                            }
                            false -> {
                                IconButton(
                                    onClick = refreshButtonCallback
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Refresh"
                                    )
                                }
                                IconButton(
                                    onClick = editButtonCallback
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit"
                                    )
                                }
                                IconButton(
                                    onClick = addButtonCallback
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
                LazyColumn(
                    modifier = Modifier
                        .nestedScroll(scrollBehaviour.nestedScrollConnection)
                        .weight(1f),
                ) {
                    itemsState.value?.let {
                        items(it) { item ->
                            AllowedInstanceListItem(
                                item = item,
                                editMode = editModeState.value,
                                deletePressedCallback = {
                                    itemDeleteRequestedCallback.invoke(item)
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
                    contentDescription = "Instance admin profile photo"
                )
            },
            trailingContent = {
                if (editMode) {
                    IconButton(
                        onClick = deletePressedCallback,
                        content = {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete"
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
                    contentDescription = "Instance admin profile photo"
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
    dialogTextFieldChangeCallback: (String) -> Unit,
    dialogDismissRequestCallback: () -> Unit,
    dialogConfirmCallback: () -> Unit
) {
    if (dialogOpenState.value) {
        Dialog(
            onDismissRequest = dialogDismissRequestCallback,
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
                        onValueChange = dialogTextFieldChangeCallback,
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
                            onClick = dialogDismissRequestCallback
                        ) {
                            Text("Cancel")
                        }
                        TextButton(
                            onClick = dialogConfirmCallback
                        ) {
                            Text("Add")
                        }
                    }
                }
            }
        }
    }
}