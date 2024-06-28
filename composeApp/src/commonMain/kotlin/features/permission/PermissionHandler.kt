package features.permission

import androidx.compose.runtime.Composable


interface PermisisonHandler {
    fun request(
    )


    fun isGranted(permission: PermissionType): Boolean

}

@Composable
expect fun rememberPermisionHandler(
    permission: PermissionType,
    callback: PermissionCallback
): PermisisonHandler