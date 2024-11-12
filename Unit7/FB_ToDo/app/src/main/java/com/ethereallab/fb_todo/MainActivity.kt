package com.ethereallab.fb_todo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.room.Room
import com.ethereallab.fb_todo.database.AppDatabase
import com.ethereallab.fb_todo.databinding.ActivityMainBinding
import com.ethereallab.fb_todo.fragments.CompletedFragment
import com.ethereallab.fb_todo.fragments.HomeFragment
import com.ethereallab.fb_todo.fragments.PendingFragment
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth and Room Database
        auth = FirebaseAuth.getInstance()
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "todo_database"
        ).build()

        // Setup bottom navigation and default fragment
        setupBottomNavigation()

        // Set default fragment to HomeFragment
        replaceFragment(HomeFragment())
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.nav_pending -> {
                    replaceFragment(PendingFragment())
                    true
                }
                R.id.nav_completed -> {
                    replaceFragment(CompletedFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    // Logout function to be called from HomeFragment
    fun logout() {
        auth.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
