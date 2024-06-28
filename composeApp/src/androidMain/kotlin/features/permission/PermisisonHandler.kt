package features.permission

import android.Manifest
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.mohaberabi.kontakts.util.contactPermissionGranted
import com.mohaberabi.kontakts.util.showContactsPermissionRationale
import features.permission.PermissionType.*


@Composable
actual fun rememberPermisionHandler(
    permission: PermissionType,
    callback: PermissionCallback
): PermisisonHandler {

    val context = LocalContext.current
    val activity = context as ComponentActivity
    val requestedPermissions = when (permission) {
        CONTACTS -> arrayOf(
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS
        )
    }
    var allowedContacts by remember {
        mutableStateOf(context.contactPermissionGranted())
    }
    var showContactsRationale by remember {
        mutableStateOf(activity.showContactsPermissionRationale())
    }
    val launcher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions(),
        ) {
            val granted = it.values.all { true }
            if (granted) {
                callback.onGranted.invoke()
            } else {
                callback.onDenied.invoke()
            }
            allowedContacts = granted
        }


    val granted = when (permission) {
        CONTACTS -> allowedContacts
    }
    val showRationale = when (permission) {
        CONTACTS -> showContactsRationale
    }

    LaunchedEffect(
        key1 = context,
    ) {
        allowedContacts = context.contactPermissionGranted()
        showContactsRationale = activity.showContactsPermissionRationale()
    }
    val handler = object : PermisisonHandler {

        override fun request() {

            if (!granted) {
                if (showRationale) {
                    callback.onUnknown.invoke()
                } else {
                    launcher.launch(requestedPermissions)
                }
            } else {
                callback.onGranted
            }

        }

        override fun isGranted(
            permission: PermissionType,
        ): Boolean = granted
    }
    return remember {
        handler
    }
}