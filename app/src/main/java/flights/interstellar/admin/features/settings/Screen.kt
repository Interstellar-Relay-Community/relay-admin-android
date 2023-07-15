package flights.interstellar.admin.features.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import flights.interstellar.admin.R
import flights.interstellar.admin.common.InterstallarAdminTheme
import flights.interstellar.admin.common.Typography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    endpointUrlValueState: State<String>,
    endpointUrlErrorMessageIdState: State<Int?>,
    endpointUrlChangeCallback: (String) -> Unit,
    apiKeyValueState: State<String>,
    apiKeyChangeCallback: (String) -> Unit,
    interstellarModeValueState: State<Boolean>,
    interstellarModeChangeCallback: (Boolean) -> Unit,
    backButtonCallback: () -> Unit
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
                                contentDescription = stringResource(id = R.string.back)
                            )
                        }
                    },
                    actions = {
                    }
                )
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = endpointUrlValueState.value,
                        onValueChange = endpointUrlChangeCallback,
                        label = {
                            Text(
                                text = stringResource(id = R.string.endpoint_url)
                            )
                        },
                        isError = endpointUrlErrorMessageIdState.value != null
                    )

                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = apiKeyValueState.value,
                        onValueChange = apiKeyChangeCallback,
                        label = {
                            Text(
                                text = stringResource(R.string.api_key)
                            )
                        }

                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                        ) {
                            Text(
                                style = Typography.bodyMedium,
                                text = stringResource(R.string.interstellar_mode)
                            )
                            Text(
                                style = Typography.bodySmall,
                                text = stringResource(R.string.enable_interstellar_features)
                            )
                        }
                        Switch(
                            checked = interstellarModeValueState.value,
                            onCheckedChange = { interstellarModeChangeCallback.invoke(it) }
                        )
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
        endpointUrlValueState = remember { mutableStateOf("") },
        endpointUrlErrorMessageIdState = remember { mutableStateOf(null) },
        endpointUrlChangeCallback = {},
        apiKeyValueState = remember { mutableStateOf("") },
        apiKeyChangeCallback = {},
        interstellarModeValueState = remember { mutableStateOf(false) },
        interstellarModeChangeCallback = {},
        backButtonCallback = {}
    )
}