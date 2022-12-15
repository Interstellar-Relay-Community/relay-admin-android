package flights.interstellar.admin.features.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import flights.interstellar.admin.R
import flights.interstellar.admin.common.InterstallarAdminTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    settingsStartCallback: () -> Unit,
    features: List<FeatureEntryDefinition>,
    featureTapCallback: (FeatureEntryDefinition) -> Unit
) {
    InterstallarAdminTheme {
        val scrollBehaviour = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            Column {
                LargeTopAppBar(
                    title = {
                        Text(
                            text = stringResource(id = R.string.title_activity_main)
                        )
                    },
                    actions = {
                        IconButton(
                            onClick = settingsStartCallback
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings"
                            )
                        }
                    },
                    scrollBehavior = scrollBehaviour
                )

                LazyColumn(
                    modifier = Modifier.nestedScroll(scrollBehaviour.nestedScrollConnection)
                ) {
                    items(features) { item ->
                        FeatureEntryItem(
                            featureEntryDefinition = item,
                            onClickCallback = {
                                featureTapCallback.invoke(item)
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeatureEntryItem(
    featureEntryDefinition: FeatureEntryDefinition,
    onClickCallback: () -> Unit
) {
    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClickCallback),
        headlineText = {
            Text(text = featureEntryDefinition.displayName)
        }
    )
    Divider()
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen(
        settingsStartCallback = {},
        features = remember { listOf() },
        featureTapCallback = {}
    )
}