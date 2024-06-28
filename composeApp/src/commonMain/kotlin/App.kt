import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import features.contacts.ContactProvider
import features.contacts.DeleteContactFailure
import features.permission.PermisisonStatus
import features.permission.PermissionCallback
import features.permission.PermissionType
import features.permission.rememberPermisionHandler
import features.settings.rememberSettingsLauncher
import kotlinx.coroutines.launch
import screen.AddContactScreen

import screen.HomeScreen


@Composable
fun App(
    contactProvider: ContactProvider,
) {
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    var permissionStatus by remember { mutableStateOf(PermisisonStatus.UNKNOWN) }
    val settingsLauncher = rememberSettingsLauncher()
    val permisisonHandler = rememberPermisionHandler(
        permission = PermissionType.CONTACTS,
        callback = PermissionCallback(
            onDenied = { permissionStatus = PermisisonStatus.DENIED },
            onGranted = { permissionStatus = PermisisonStatus.GRANTED },
        )
    )
    LaunchedEffect(
        Unit,
    ) {
        permisisonHandler.request(
        )
    }




    MaterialTheme {

        Scaffold(
            snackbarHost = { SnackbarHost(snackBarHostState) },
        ) { _ ->
            when (permissionStatus) {
                PermisisonStatus.GRANTED -> {
                    HomeScreen(
                        contactProvider = contactProvider,
                        onShowSnackBar = {
                            scope.launch {
                                snackBarHostState.showSnackbar(it)
                            }
                        }
                    )
                }

                else -> PlaceHolder(
                    onOpenSettings = { settingsLauncher.launch() },
                )
            }

        }


    }


}


@Composable
fun PlaceHolder(
    modifier: Modifier = Modifier,
    onOpenSettings: () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("Permissions is not granted please allow from settings")
        Button(onClick = onOpenSettings) {
            Text("Open Settings")
        }

    }
}