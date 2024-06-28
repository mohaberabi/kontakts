package features.contacts

import android.content.ContentProviderOperation
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.ArrayList


actual class ContactProvider(
    private val context: Context,
) {

    companion object {
        private val CONTACTS_STORE = ContactsContract.CommonDataKinds.Phone.CONTENT_URI

        private val CONTACTS_FIELDS = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        private val ACCOUNT_META =
            ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build()
    }

    actual suspend fun getAllContacts(): List<ContactModel> {
        return withContext(Dispatchers.IO) {
            try {
                val result = mutableListOf<ContactModel>()
                val contentResolver = context.contentResolver
                val cursor: Cursor? = contentResolver.query(
                    CONTACTS_STORE,
                    CONTACTS_FIELDS,
                    null, null, null
                )
                cursor?.use {
                    val nameIndex = it.getColumnIndex(CONTACTS_FIELDS[0])
                    val numberIndex = it.getColumnIndex(CONTACTS_FIELDS[1])
                    while (it.moveToNext()) {
                        val name = it.getString(nameIndex)
                        val phone = it.getString(numberIndex)
                        result.add(ContactModel(name = name, phone = phone))
                    }
                }
                result
            } catch (e: Exception) {
                e.printStackTrace()
                throw FetchContactFailure(e.message ?: "Unknown Error Getting Contacts")
            }
        }
    }

    actual suspend fun addContact(contact: ContactModel) {
        withContext(Dispatchers.IO) {
            try {
                val resolver = context.contentResolver
                val operations = ArrayList<ContentProviderOperation>()
                    .apply {
                        add(ACCOUNT_META)
                        add(displayNameOperation(contact.name))
                        add(numberOperation(contact.phone))
                    }
                resolver.applyBatch(ContactsContract.AUTHORITY, operations)
            } catch (e: Exception) {
                e.printStackTrace()
                throw AddContactFailure(e.message ?: "Unknown Error Adding Contact")
            }
        }
    }

    actual suspend fun deleteContact(phone: String) {
        withContext(Dispatchers.IO) {
            try {
                val resolver = context.contentResolver
                val uri: Uri = Uri.withAppendedPath(
                    ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                    Uri.encode(phone)
                )

                val cursor: Cursor? = resolver.query(
                    uri, arrayOf(ContactsContract.PhoneLookup._ID),
                    null, null, null
                )
                cursor?.use {
                    if (it.moveToNext()) {
                        val idIndex = it.getColumnIndex(ContactsContract.PhoneLookup._ID)
                        val contactId = it.getString(idIndex)
                        val deleteUri = Uri.withAppendedPath(
                            ContactsContract.Contacts.CONTENT_URI,
                            contactId
                        )
                        resolver.delete(deleteUri, null, null)
                    } else {
                        throw DeleteContactFailure("Contact is not found ")

                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                throw DeleteContactFailure(e.message ?: "Unknown Error Adding Contact")

            }
        }
    }


    private fun displayNameOperation(name: String): ContentProviderOperation {
        return ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
            .withValue(
                ContactsContract.Data.MIMETYPE,
                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
            )
            .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
            .build()
    }

    private fun numberOperation(phone: String): ContentProviderOperation {
        return ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
            .withValue(
                ContactsContract.Data.MIMETYPE,
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
            )
            .withValue(
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                phone
            )
            .withValue(
                ContactsContract.CommonDataKinds.Phone.TYPE,
                ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
            )
            .build()
    }
}