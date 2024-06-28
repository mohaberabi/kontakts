package com.mohaberabi.kontakts

import App
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import features.contacts.ContactProvider

class MainActivity : ComponentActivity() {


    private val contactsProvider by lazy { ContactProvider(this.applicationContext) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            App(
                contactProvider = contactsProvider,
            )
        }
    }
}

