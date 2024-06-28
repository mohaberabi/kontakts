package features.contacts

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import platform.Contacts.CNAuthorizationStatusAuthorized
import platform.Contacts.CNContact
import platform.Contacts.CNContactFetchRequest
import platform.Contacts.CNContactGivenNameKey
import platform.Contacts.CNContactIdentifierKey
import platform.Contacts.CNContactPhoneNumbersKey
import platform.Contacts.CNContactStore
import platform.Contacts.CNEntityType
import platform.Contacts.CNLabelPhoneNumberMobile
import platform.Contacts.CNLabeledValue
import platform.Contacts.CNMutableContact
import platform.Contacts.CNPhoneNumber
import platform.Contacts.CNSaveRequest
import platform.Contacts.predicateForContactsMatchingPhoneNumber
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


actual class ContactProvider {


    private val contactStore by lazy { CNContactStore() }

    @OptIn(ExperimentalForeignApi::class)
    actual suspend fun deleteContact(phone: String) {

        withContext(Dispatchers.IO) {
            try {
                val saveRequest = CNSaveRequest()
                val predicate = CNContact.predicateForContactsMatchingPhoneNumber(
                    CNPhoneNumber(phone),
                )
                val keysToFetch = listOf(CNContactPhoneNumbersKey)

                val contacts = contactStore.unifiedContactsMatchingPredicate(
                    predicate = predicate,
                    keysToFetch = keysToFetch,
                    null
                )
                contacts?.let {
                    if (it.isNotEmpty()) {
                        val contact = it.first() as CNContact
                        val copied = contact.mutableCopy() as CNMutableContact
                        saveRequest.deleteContact(copied)
                        contactStore.executeSaveRequest(saveRequest, null)
                    }
                } ?: throw DeleteContactFailure("No Matching contacts with given phone number ")
            } catch (e: Exception) {
                e.printStackTrace()
                throw DeleteContactFailure("${e.message}")
            }
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    actual suspend fun addContact(contact: ContactModel) {
        withContext(Dispatchers.IO) {
            try {
                val saveRequest = CNSaveRequest()
                val newContact = CNMutableContact()
                newContact.setGivenName(contact.name)
                val phoneNumber = CNLabeledValue(
                    label = CNLabelPhoneNumberMobile,
                    value = CNPhoneNumber(stringValue = contact.phone)
                )
                newContact.setPhoneNumbers(listOf(phoneNumber))
                saveRequest.addContact(newContact, null)
                contactStore.executeSaveRequest(
                    saveRequest,
                    null
                )
            } catch (e: Exception) {

                e.printStackTrace()
                throw AddContactFailure("${e.message} Failed adding  contact")
            }

        }
    }

    actual suspend fun getAllContacts(): List<ContactModel> {
        return withContext(Dispatchers.IO) {
            suspendCoroutine { cont ->
                val authState =
                    CNContactStore.authorizationStatusForEntityType(CNEntityType.CNEntityTypeContacts)
                if (authState == CNAuthorizationStatusAuthorized) {
                    resumeWithContacts(contactStore, cont)
                } else {
                    contactStore.requestAccessForEntityType(CNEntityType.CNEntityTypeContacts) { granted, _ ->
                        if (granted) {
                            resumeWithContacts(contactStore, cont)
                        } else {
                            cont.resumeWithException(FetchContactFailure("Permission is not granted"))
                        }
                    }
                }
            }
        }

    }


    @OptIn(ExperimentalForeignApi::class)
    private fun resumeWithContacts(
        store: CNContactStore,
        cont: Continuation<List<ContactModel>>,
    ) {
        val keys = listOf(CNContactGivenNameKey, CNContactPhoneNumbersKey)
        val request = CNContactFetchRequest(keysToFetch = keys)
        val contactsRes = mutableListOf<ContactModel>()
        store.enumerateContactsWithFetchRequest(
            fetchRequest = request,
            error = null
        ) { contact, _ ->

            val name = contact?.givenName ?: "Unknown"
            val phone = contact?.phoneNumbers?.let {
                val first = it.firstOrNull() as? CNLabeledValue
                val cnPhone = first?.value as? CNPhoneNumber
                cnPhone?.stringValue
            }
            val appContact = ContactModel(name = name, phone = phone ?: "Unknown")
            contactsRes.add(appContact)
        }
        cont.resume(contactsRes)
    }
}