package screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import features.contacts.AddContactFailure
import features.contacts.ContactModel
import features.contacts.ContactProvider


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddContactScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    contactProvider: ContactProvider,
    onShowSnackBar: (String) -> Unit,
) {


    var loading by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var addContact by remember { mutableStateOf(false) }
    val phoneFocusRequester = remember { FocusRequester() }
    val keyBoardController = LocalSoftwareKeyboardController.current
    LaunchedEffect(
        addContact,
    ) {
        if (addContact) {
            try {
                loading = true
                val contact = ContactModel(name = name, phone = phone)
                contactProvider.addContact(contact)
                loading = false
                onShowSnackBar("Contact Added")
            } catch (e: AddContactFailure) {
                loading = false
                onShowSnackBar(e.message)
            }
            addContact = false
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = ""
                        )
                    }
                },
                title = {
                    Text("Add Contact")
                },
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .padding(padding)
                .padding(16.dp),
        ) {


            OutlinedTextField(
                value = name,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ), keyboardActions = KeyboardActions(
                    onNext = {
                        phoneFocusRequester.requestFocus()
                    }
                ),
                label = { Text("Name") },
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.None
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyBoardController?.hide()
                    }
                ),
                value = phone, label = { Text("Phone ") },
                onValueChange = {
                    if (it.all { it.isDigit() }) {
                        phone = it
                    }
                },
                modifier = Modifier.fillMaxWidth()
                    .focusRequester(phoneFocusRequester),
            )
            Spacer(Modifier.height(8.dp))

            Button(
                enabled = name.isNotEmpty() && phone.isNotEmpty(),
                onClick = {
                    addContact = true
                },
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White
                    )
                } else {
                    Text("Save")
                }

            }
        }
    }
}