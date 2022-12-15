package flights.interstellar.admin.features.connectedInstance

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import flights.interstellar.admin.R
import flights.interstellar.admin.api.client.apClient
import flights.interstellar.admin.api.pojo.InstanceInfo
import flights.interstellar.admin.common.InterstallarAdminTheme
import flights.interstellar.admin.common.Purple80

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    backButtonCallback: () -> Unit,
    refreshButtonCallback: () -> Unit,
    itemsState: State<List<ConnectedInstanceItem>?>
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
                            text = stringResource(id = R.string.title_activity_connectedInstance)
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
                        IconButton(
                            onClick = refreshButtonCallback
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh"
                            )
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
                            ConnectedInstanceListItem(item)
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
    }
}


@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen(
        refreshButtonCallback = {},
        backButtonCallback = {},
        itemsState = remember {
            mutableStateOf(
                listOf(
                    ConnectedInstanceItem(instanceUrl = "example.com"),
                    ConnectedInstanceItem(instanceUrl = "example2.com"),
                    ConnectedInstanceItem(instanceUrl = "example3.com")
                )
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectedInstanceListItem(item: ConnectedInstanceItem) {
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
            }
        )
        Divider()
    }

    LaunchedEffect(Unit) {
        val instanceInfo = try {
            apClient.getInstanceInfo(item.instanceUrl)
        } catch (_: Exception) {
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
                    contentDescription = "Instance admin profile photo"
                )
            }
        )
        Divider()
    }
}