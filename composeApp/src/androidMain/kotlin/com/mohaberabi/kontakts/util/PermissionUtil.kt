package com.mohaberabi.kontakts.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat


fun Context.isPermissionGranted(permission: String): Boolean =
    ContextCompat.checkSelfPermission(
        this,
        permission,
    ) == PackageManager.PERMISSION_GRANTED


fun Activity.showPermissionRationale(permission: String): Boolean =
    shouldShowRequestPermissionRationale(permission)


fun Activity.showContactsPermissionRationale(): Boolean =
    showPermissionRationale(Manifest.permission.READ_CONTACTS)
            || showPermissionRationale(Manifest.permission.WRITE_CONTACTS)

fun Context.contactPermissionGranted(): Boolean =
    isPermissionGranted(Manifest.permission.READ_CONTACTS)
            && isPermissionGranted(Manifest.permission.WRITE_CONTACTS)