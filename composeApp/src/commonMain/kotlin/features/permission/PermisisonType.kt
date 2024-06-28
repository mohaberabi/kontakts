package features.permission


enum class PermissionType {
    CONTACTS
}

enum class PermisisonStatus {
    GRANTED,
    DENIED,
    UNKNOWN;


    val isGranted: Boolean
        get() = this == GRANTED
    val isDenied: Boolean
        get() = this == DENIED
    val isUnknown: Boolean
        get() = this == UNKNOWN
}

data class PermissionCallback(
    val onGranted: () -> Unit = {},
    val onDenied: () -> Unit = {},
    val onUnknown: () -> Unit = {},
)