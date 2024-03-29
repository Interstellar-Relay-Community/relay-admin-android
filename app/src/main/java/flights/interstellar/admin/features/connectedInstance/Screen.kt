package flights.interstellar.admin.features.connectedInstance

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults.exitUntilCollapsedScrollBehavior
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.SecureFlagPolicy
import androidx.lifecycle.lifecycleScope
import coil.compose.AsyncImage
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import flights.interstellar.admin.R
import flights.interstellar.admin.api.pojo.AuthorityConfig
import flights.interstellar.admin.api.pojo.InstanceInfo
import flights.interstellar.admin.common.InterstallarAdminTheme
import flights.interstellar.admin.common.Purple80
import flights.interstellar.admin.repository.instanceInfoRepository
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    backButtonCallback: suspend () -> Unit,
    refreshButtonCallback: suspend () -> Unit,
    snackbarState: SnackbarHostState,
    itemsState: State<List<ConnectedInstanceItem>?>,
    onItemClickListener: (ConnectedInstanceItem) -> Unit,
    authorityConfigDialogDisplayState: State<Pair<ConnectedInstanceItem, AuthorityConfig?>?>,
    authorityConfigConfirmCallback: suspend (ConnectedInstanceItem, AuthorityConfig?) -> Unit,
    authorityConfigDismissCallback: suspend () -> Unit,
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
                            text = stringResource(id = R.string.title_activity_connectedInstance)
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { lifecycleScope.launch { backButtonCallback.invoke() } }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = stringResource(id = R.string.back)
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { lifecycleScope.launch { refreshButtonCallback.invoke() } }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = stringResource(id = R.string.refresh)
                            )
                        }
                    },
                    scrollBehavior = scrollBehaviour
                )
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarState)
            }
        ) { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues)) {
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
                            ConnectedInstanceListItem(item, onItemClickListener)
                        }
                    } ?: run {
                        item {
                            repeat(3) {
                                ConnectedInstanceListSkeletonItem()
                            }
                        }
                    }
                }
            }
        }

        if (authorityConfigDialogDisplayState.value != null) {
            val authorityConfig =
                remember { mutableStateOf(authorityConfigDialogDisplayState.value?.second) }

            AlertDialog(
                properties = DialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true,
                    securePolicy = SecureFlagPolicy.Inherit
                ),
                title = {
                    Text(
                        text = "Authority config"
                    )
                },
                confirmButton = {
                    Text(
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = LocalIndication.current,
                            onClick = {
                                lifecycleScope.launch {
                                    authorityConfigConfirmCallback.invoke(
                                        authorityConfigDialogDisplayState.value!!.first,
                                        authorityConfig.value
                                    )
                                }
                            }
                        ),
                        text = stringResource(R.string.confirm),
                    )
                },
                dismissButton = {
                    Text(
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = LocalIndication.current,
                            onClick = {
                                lifecycleScope.launch {
                                    authorityConfigDismissCallback.invoke()
                                }
                            }
                        ),
                        text = stringResource(R.string.dismiss)
                    )
                },
                text = {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(1f),
                        value = authorityConfig.value?.probability?.toString() ?: "",
                        onValueChange = {
                            try {
                                authorityConfig.value =
                                    if (authorityConfig.value == null) {
                                        // Create a new one
                                        AuthorityConfig(
                                            enableProbability = true,
                                            probability = it.toInt(),
                                            authoritySet = listOf(),
                                            isAllowList = false,
                                            receiveOnly = false
                                        )
                                    } else {
                                        authorityConfig.value!!.copy(
                                            enableProbability = true,
                                            probability = it.toInt()
                                        )
                                    }
                            } catch (_: NumberFormatException) {
                                if (it.isBlank())
                                    authorityConfig.value = null
                            }

                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        )
                    )
                },
                onDismissRequest = {
                    lifecycleScope.launch {
                        authorityConfigDismissCallback.invoke()
                    }
                }
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen(
        refreshButtonCallback = {},
        backButtonCallback = {},
        snackbarState = remember { SnackbarHostState() },
        itemsState = remember {
            mutableStateOf(
                listOf(
                    ConnectedInstanceItem(
                        instanceUrl = "example.com", authorityConfig = AuthorityConfig(
                            probability = 178,
                            enableProbability = true,
                            authoritySet = listOf(),
                            isAllowList = false,
                            receiveOnly = false
                        )
                    ),
                    ConnectedInstanceItem(instanceUrl = "example2.com", authorityConfig = null),
                    ConnectedInstanceItem(instanceUrl = "example3.com", authorityConfig = null)
                )
            )
        },
        onItemClickListener = {},
        authorityConfigDialogDisplayState = remember { mutableStateOf(null) },
        authorityConfigConfirmCallback = { _, _ -> },
        authorityConfigDismissCallback = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectedInstanceListItem(
    item: ConnectedInstanceItem,
    onItemClickListener: (ConnectedInstanceItem) -> Unit
) {
    val apLoadingCompleteState = remember { mutableStateOf(false) }
    val instanceInfoState = remember { mutableStateOf<InstanceInfo?>(null) }

    Column {
        ListItem(
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = LocalIndication.current,
                onClick = { onItemClickListener.invoke(item) },
            ),
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
                Row {
                    if (item.authorityConfig?.enableProbability == true) {
                        Text(
                            "Prob: ${
                                "%.2f".format((item.authorityConfig.probability / 255.0f) * 100)
                            }%"
                        )
                    }
                }
            }
        )
        Divider()
    }

    LaunchedEffect(item.instanceUrl) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectedInstanceListSkeletonItem() {
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