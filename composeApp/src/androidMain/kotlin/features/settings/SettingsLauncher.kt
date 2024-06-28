package features.settings

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.net.URI


actual class SettingsLauncher actual constructor(private val onLaunch: () -> Unit) {


    actual fun launch() {
        onLaunch()
    }
}


@Composable

actual fun rememberSettingsLauncher(): SettingsLauncher {
    val context = LocalContext.current
    return remember {
        SettingsLauncher {
            Intent(
                Settings.ACTION_APPLICATION_SETTINGS,
                Uri.fromParts("package", context.packageName, null)
            ).also {
                context.startActivity(it)
            }
        }

    }
}