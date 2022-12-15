package flights.interstellar.admin.features.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import flights.interstellar.admin.R
import flights.interstellar.admin.common.InterstallarAdminTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    apiKeyValueState: State<String>,
    apiKeyChangeCallback: (String) -> Unit,
    backButtonCallback: () -> Unit,
    doneButtonCallback: () -> Unit
) {
    InterstallarAdminTheme {
        Surface {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(id = R.string.title_activity_setting)
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
                            onClick = doneButtonCallback
                        ) {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = "Done"
                            )
                        }
                    }
                )
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = apiKeyValueState.value,
                        onValueChange = apiKeyChangeCallback,
                        label = {
                            Text(
                                text = "API key"
                            )
                        }

                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen(
        apiKeyValueState = remember { mutableStateOf("") },
        apiKeyChangeCallback = {},
        backButtonCallback = {},
        doneButtonCallback = {}
    )
}