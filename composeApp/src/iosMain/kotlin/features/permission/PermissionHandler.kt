package features.permission

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.Contacts.CNContactStore
import platform.Contacts.CNEntityType


class IOSPermisisonHandler(
    private val permission: PermissionType,
    private val callback: PermissionCallback
) : PermisisonHandler {


    override fun isGranted(permission: PermissionType): Boolean =
        contactPermissionStatus().isGranted

    override fun request(
    ) {

        when (permission) {
            PermissionType.CONTACTS -> {
                val status = contactPermissionStatus()
                requestContacts(status = status, callback = callback)
            }
        }
    }

    private fun contactPermissionStatus(): PermisisonStatus {
        val authState =
            CNContactStore.authorizationStatusForEntityType(CNEntityType.CNEntityTypeContacts)
        return authState.toAppPermisison()

    }

    private fun requestContacts(
        status: PermisisonStatus,
        callback: PermissionCallback
    ) {
        when (status) {
            PermisisonStatus.GRANTED -> callback.onGranted.invoke()
            PermisisonStatus.DENIED -> callback.onDenied.invoke()
            PermisisonStatus.UNKNOWN -> {
                val contactStore = CNContactStore()
                contactStore.requestAccessForEntityType(CNEntityType.CNEntityTypeContacts) { granted, _ ->
                    if (granted) {
                        callback.onGranted.invoke()
                    } else {
                        callback.onDenied.invoke()
                    }
                }
            }
        }
    }
}

@Composable
actual fun rememberPermisionHandler(
    permission: PermissionType,
    callback: PermissionCallback
): PermisisonHandler =
    remember { IOSPermisisonHandler(permission = permission, callback = callback) }