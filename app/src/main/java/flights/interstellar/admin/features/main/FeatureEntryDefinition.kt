package flights.interstellar.admin.features.main

import android.app.Activity

data class FeatureEntryDefinition(
    val displayName: String,
    val targetClass: Class<out Activity>
)