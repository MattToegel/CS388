package com.ethereallab.recyclerexamples.models


// added id which wasn't explicitly noted in the lesson (using a default value so earlier and later examples work
class Contact(val name: String, val isOnline: Boolean, val id: Int) {

    companion object {
        private var lastContactId = 0
        fun createContactsList(numContacts: Int): ArrayList<Contact> {
            val contacts = ArrayList<Contact>()
            for (i in 1..numContacts) {
                contacts.add(Contact("Person " + ++lastContactId, i <= numContacts / 2,
                    lastContactId))
            }
            return contacts
        }
    }
}