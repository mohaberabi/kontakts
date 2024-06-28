package features.contacts

data class AddContactFailure(override val message: String) : Throwable(message = message)

data class FetchContactFailure(override val message: String) : Throwable(message = message)
data class DeleteContactFailure(override val message: String) : Throwable(message = message)

expect class ContactProvider {

    suspend fun deleteContact(phone: String)

    suspend fun getAllContacts(): List<ContactModel>


    suspend fun addContact(contact: ContactModel)
}