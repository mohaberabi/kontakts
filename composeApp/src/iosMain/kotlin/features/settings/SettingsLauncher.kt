package features.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString


actual class SettingsLauncher actual constructor(
    private val onLaunch: () -> Unit,
) {
    actual fun launch() {
        onLaunch()
    }

}


@Composable

actual fun rememberSettingsLauncher(): SettingsLauncher {
    return remember {
        SettingsLauncher {
            NSURL.URLWithString(UIApplicationOpenSettingsURLString)?.let {
                UIApplication.sharedApplication().openURL(it)
            }
        }
    }
}