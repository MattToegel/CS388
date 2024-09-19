package com.ethereallab.recyclerexamples

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ethereallab.recyclerexamples.adapters.ContactsAdapterListAdapter
import com.ethereallab.recyclerexamples.models.Contact

class UserListActivityListAdapter : AppCompatActivity() {

    // Use ListAdapter for managing the list
    private lateinit var contactsAdapterListAdapter: ContactsAdapterListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users)

        // Find the TextView by its ID
        val textViewExampleTitle = findViewById<TextView>(R.id.textViewExampleTitle)

        // Update the text based on the current activity
        textViewExampleTitle.text = "Example 2 - UserListActivityListAdapter"

        // Lookup the recyclerview in activity layout
        val rvContacts = findViewById<RecyclerView>(R.id.rvContacts)

        // Initialize the ListAdapter
        contactsAdapterListAdapter = ContactsAdapterListAdapter()

        // Attach the adapter to the recyclerview to populate items
        rvContacts.adapter = contactsAdapterListAdapter

        // Set layout manager to position the items
        rvContacts.layoutManager = LinearLayoutManager(this)

        // Initialize contacts and submit the list to the adapter
        val contacts = Contact.createContactsList(20)
        contactsAdapterListAdapter.submitList(contacts)

        // Set up button click listener to add more items
        val moreButton = findViewById<View>(R.id.buttonAddMore)
        moreButton.setOnClickListener {
            // Get current list from adapter
            val currentList = contactsAdapterListAdapter.currentList.toMutableList()

            // Add more contacts to the list
            val newContacts = Contact.createContactsList(5)
            currentList.addAll(newContacts)

            // Submit the updated list to the adapter
            contactsAdapterListAdapter.submitList(currentList)
        }

        // Hide the Example 2 button when in UserListActivityListAdapter
        val buttonExample2 = findViewById<Button>(R.id.buttonExample2)
        buttonExample2.visibility = View.GONE

        val buttonExample1 = findViewById<Button>(R.id.buttonExample1)
        buttonExample1.setOnClickListener {
            startActivity(Intent(this, UserListActivity::class.java)) // Navigate to Example 1
        }

        val buttonExample3 = findViewById<Button>(R.id.buttonExample3)
        buttonExample3.setOnClickListener {
            startActivity(Intent(this, UserListAdvancedActivity::class.java)) // Navigate to Example 3
        }

    }
}
