package flights.interstellar.admin.common

import android.app.Activity
import android.content.Context
import android.content.Intent

fun Context.openActivity(clazz: Class<out Activity>) {
    Intent(this, clazz).apply {
        startActivity(this)
    }
}