package features.permission

import platform.Contacts.CNAuthorizationStatus
import platform.Contacts.CNAuthorizationStatusAuthorized
import platform.Contacts.CNAuthorizationStatusDenied
import platform.Contacts.CNAuthorizationStatusNotDetermined
import platform.Contacts.CNAuthorizationStatusRestricted


fun CNAuthorizationStatus.toAppPermisison(): PermisisonStatus {
    return when (this) {
        CNAuthorizationStatusAuthorized -> PermisisonStatus.GRANTED
        CNAuthorizationStatusNotDetermined -> PermisisonStatus.UNKNOWN
        else -> PermisisonStatus.DENIED
    }
}