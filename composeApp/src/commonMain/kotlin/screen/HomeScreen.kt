package screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import features.contacts.ContactModel
import features.contacts.ContactProvider
import features.contacts.DeleteContactFailure
import features.contacts.FetchContactFailure


enum class ContactStatus {
    INITIAL,
    LOADING,
    ERROR,
    POPULATED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    contactProvider: ContactProvider,
    onShowSnackBar: (String) -> Unit,
) {
    var showAddContactScreen by remember { mutableStateOf(false) }
    var reload by remember { mutableStateOf(true) }
    var confirmDelete by remember { mutableStateOf(false) }
    var choosedContact by remember { mutableStateOf<String?>(null) }
    var status by remember { mutableStateOf(ContactStatus.INITIAL) }
    var error by remember { mutableStateOf("") }
    var contactsList by remember { mutableStateOf(listOf<ContactModel>()) }
    LaunchedEffect(
        reload,
    ) {
        if (reload) {
            try {
                status = ContactStatus.LOADING
                contactsList = contactProvider.getAllContacts()
                status = ContactStatus.POPULATED
            } catch (e: FetchContactFailure) {
                error = e.message
                status = ContactStatus.ERROR
            }
            if (reload) {
                reload = false
            }
        }

    }

    LaunchedEffect(
        confirmDelete,
    ) {
        if (confirmDelete) {
            try {
                contactProvider.deleteContact(choosedContact!!)
                onShowSnackBar("Contact Deleted")
                choosedContact = null
                reload = true
            } catch (e: DeleteContactFailure) {
                onShowSnackBar("${e.message}: Error Deleting Contact")
            }
            confirmDelete = false
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text("Contacts App")
                    },
                    actions = {
                        IconButton(
                            onClick = { showAddContactScreen = true },
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = ""
                            )
                        }
                        IconButton(
                            onClick = {
                                reload = true
                            },
                        ) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = ""
                            )
                        }
                    }
                )
            }
        ) { padding ->

            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                when (status) {

                    ContactStatus.ERROR -> Text(error)
                    ContactStatus.POPULATED -> LazyColumn {
                        items(contactsList) { contact ->

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .weight(0.85f),
                                    horizontalAlignment = Alignment.Start,
                                ) {
                                    Text(
                                        contact.name,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        style = MaterialTheme.typography.titleLarge,
                                    )
                                    Spacer(
                                        modifier = Modifier
                                            .height(12.dp)
                                    )
                                    Text(
                                        contact.phone,
                                        style = MaterialTheme.typography.bodyLarge,
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        confirmDelete = true
                                        choosedContact = contact.phone
                                    },
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "delete"
                                    )
                                }
                            }
                        }
                    }

                    else -> CircularProgressIndicator()
                }
            }

        }
        if (showAddContactScreen) {
            AddContactScreen(
                onShowSnackBar = onShowSnackBar,
                onBackClick = { showAddContactScreen = false },
                contactProvider = contactProvider,
            )
        }
    }

}

