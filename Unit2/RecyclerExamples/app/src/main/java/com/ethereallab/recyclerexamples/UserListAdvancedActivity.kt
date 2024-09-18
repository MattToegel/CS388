package com.ethereallab.recyclerexamples

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ethereallab.recyclerexamples.adapters.ContactsAdapterListAdapter
import com.ethereallab.recyclerexamples.models.Contact
import com.ethereallab.recyclerexamples.utils.EndlessRecyclerViewScrollListener
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator

class UserListAdvancedActivity : AppCompatActivity() {

    private lateinit var contactsAdapter: ContactsAdapterListAdapter
    private lateinit var contacts: ArrayList<Contact>
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list_advanced) // Use the new XML layout

        // Find the TextView by its ID
        val textViewExampleTitle = findViewById<TextView>(R.id.textViewExampleTitle)

        // Update the text based on the current activity
        textViewExampleTitle.text = "Example 3 - UserListAdvancedActivity"

        // Initialize contacts and adapter
        contacts = Contact.createContactsList(20)
        contactsAdapter = ContactsAdapterListAdapter()

        // Find RecyclerView and SwipeRefreshLayout
        recyclerView = findViewById(R.id.rvContacts)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)

        // Set fixed size for RecyclerView
        recyclerView.setHasFixedSize(true)

        // Setup the layout manager with vertical orientation
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        layoutManager.scrollToPosition(0) // Start at the top
        recyclerView.layoutManager = layoutManager

        // Set item animator for RecyclerView
        recyclerView.itemAnimator = SlideInUpAnimator()

        // Attach adapter to RecyclerView
        recyclerView.adapter = contactsAdapter
        contactsAdapter.submitList(contacts)

        // Setup endless scrolling
        recyclerView.addOnScrollListener(object : EndlessRecyclerViewScrollListener(layoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                fetchMoreContacts() // Method to fetch and append more data
            }
        })

        // Setup pull to refresh
        swipeRefreshLayout.setOnRefreshListener {
            refreshContacts() // Method to refresh data
        }

        // Setup touch event handling
        recyclerView.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                // Intercept the touch event when the user touches down on the screen
                return e.action == MotionEvent.ACTION_DOWN
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
                Log.d("TouchEvent", "onTouchEvent: $e")
                if (e.action == MotionEvent.ACTION_DOWN) {  // Ensure that we handle only the down action
                    val view = rv.findChildViewUnder(e.x, e.y)
                    if (view != null) {
                        val position = rv.getChildAdapterPosition(view)
                        if (position != RecyclerView.NO_POSITION) {
                            val contact = contacts[position]
                            Toast.makeText(
                                this@UserListAdvancedActivity,
                                "Clicked: ${contact.name}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
                // Handle request to disallow intercepting touch events
            }
        })

        // Set up button listeners
        val buttonExample1 = findViewById<Button>(R.id.buttonExample1)
        buttonExample1.setOnClickListener {
            startActivity(Intent(this, UserListActivity::class.java)) // Navigate to Example 1
        }

        val buttonExample2 = findViewById<Button>(R.id.buttonExample2)
        buttonExample2.setOnClickListener {
            startActivity(Intent(this, UserListActivityListAdapter::class.java)) // Navigate to Example 2
        }

        // Add more contacts when the "Add More Contacts" button is clicked
        val buttonAddMore = findViewById<Button>(R.id.buttonAddMore)
        buttonAddMore.setOnClickListener {
            fetchMoreContacts() // Append more contacts to the list
        }
    }

    // Method to fetch more contacts (endless scrolling and button click)
    private fun fetchMoreContacts() {
        val moreContacts = Contact.createContactsList(5) // Fetch or generate more contacts
        contacts.addAll(moreContacts)
        contactsAdapter.submitList(ArrayList(contacts)) // Update adapter's list
    }

    // Method to refresh contacts (pull to refresh)
    private fun refreshContacts() {
        contacts.clear()
        contacts.addAll(Contact.createContactsList(20)) // Re-fetch or generate contacts
        contactsAdapter.submitList(ArrayList(contacts))
        swipeRefreshLayout.isRefreshing = false // Stop the refreshing animation
    }
}
