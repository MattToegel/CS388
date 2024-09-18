package com.ethereallab.recyclerexamples

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ethereallab.recyclerexamples.adapters.ContactsAdapter
import com.ethereallab.recyclerexamples.models.Contact

class UserListActivity : AppCompatActivity() {
    lateinit var contacts: ArrayList<Contact>
    override fun onCreate(savedInstanceState: Bundle?) {
        // don't forget to add the super call
        super.onCreate(savedInstanceState)
        // this too wasn't explicit in the lesson
        setContentView(R.layout.activity_users)
        // Find the TextView by its ID
        val textViewExampleTitle = findViewById<TextView>(R.id.textViewExampleTitle)

        // Update the text based on the current activity
        textViewExampleTitle.text = "Example 1 - UserListActivity"

        // Lookup the recyclerview in activity layout
        val rvContacts = findViewById<View>(R.id.rvContacts) as RecyclerView
        // Initialize contacts
        contacts = Contact.createContactsList(20)
        // Create adapter passing in the sample user data
        val adapter = ContactsAdapter(contacts)
        // Attach the adapter to the recyclerview to populate items
        rvContacts.adapter = adapter
        // Set layout manager to position the items
        rvContacts.layoutManager = LinearLayoutManager(this)
        // That's all!

        // Not explicit in lesson
        var moreButton = findViewById<View>(R.id.buttonAddMore);
        moreButton.setOnClickListener { v ->
            val curSize = adapter.getItemCount()
            // add to the existing list
            contacts.addAll(Contact.createContactsList(5))
            adapter.notifyItemRangeInserted(curSize, contacts.size)
        }

        // Hide the Example 1 button when in UserListActivity
        val buttonExample1 = findViewById<Button>(R.id.buttonExample1)
        buttonExample1.visibility = View.GONE

        val buttonExample2 = findViewById<Button>(R.id.buttonExample2)
        buttonExample2.setOnClickListener {
            startActivity(Intent(this, UserListActivityListAdapter::class.java)) // Navigate to Example 2
        }

        val buttonExample3 = findViewById<Button>(R.id.buttonExample3)
        buttonExample3.setOnClickListener {
            startActivity(Intent(this, UserListAdvancedActivity::class.java)) // Navigate to Example 3
        }
    }
}