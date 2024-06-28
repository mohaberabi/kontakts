import androidx.compose.ui.window.ComposeUIViewController
import features.contacts.ContactProvider

fun MainViewController() = ComposeUIViewController {
    App(
        contactProvider = ContactProvider()
    )
}