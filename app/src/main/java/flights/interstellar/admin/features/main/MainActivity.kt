package flights.interstellar.admin.features.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import flights.interstellar.admin.common.openActivity
import flights.interstellar.admin.features.allowedInstance.AllowedInstanceActivity
import flights.interstellar.admin.features.connectedInstance.ConnectedInstanceActivity
import flights.interstellar.admin.features.settings.SettingActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen(
                settingsStartCallback = {
                    openActivity(SettingActivity::class.java)
                },
                features = remember {
                    listOf(
                        FeatureEntryDefinition(
                            displayName = "Connected instances",
                            targetClass = ConnectedInstanceActivity::class.java
                        ),
                        FeatureEntryDefinition(
                            displayName = "Allowed instances",
                            targetClass = AllowedInstanceActivity::class.java
                        )
                    )
                },
                featureTapCallback = {
                    openActivity(it.targetClass)
                }
            )
        }
    }
}

