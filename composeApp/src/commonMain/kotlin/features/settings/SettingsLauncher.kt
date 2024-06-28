package features.settings

import androidx.compose.runtime.Composable


expect class SettingsLauncher(onLaunch: () -> Unit) {


    fun launch()
}


@Composable
expect fun rememberSettingsLauncher(): SettingsLauncher