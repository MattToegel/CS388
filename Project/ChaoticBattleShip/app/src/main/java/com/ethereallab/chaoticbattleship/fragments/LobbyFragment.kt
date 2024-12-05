package com.ethereallab.chaoticbattleship.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ethereallab.chaoticbattleship.LobbyItem
import com.ethereallab.chaoticbattleship.databinding.FragmentLobbyBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LobbyFragment : Fragment() {

    private var _binding: FragmentLobbyBinding? = null
    private val binding get() = _binding!!
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLobbyBinding.inflate(inflater, container, false)

        // Ensure user is logged in
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(requireContext(), "You must be logged in to create or search lobbies", Toast.LENGTH_SHORT).show()
            return binding.root
        }

        setupListeners()

        return binding.root
    }

    private fun setupListeners() {
        binding.createLobbyButton.setOnClickListener {
            val lobbyName = binding.lobbyNameEditText.text.toString().trim()
            val maxPlayers = binding.maxPlayersEditText.text.toString().trim().toIntOrNull()

            if (lobbyName.isEmpty() || maxPlayers == null || maxPlayers < 2) {
                Toast.makeText(requireContext(), "Enter a valid lobby name and max players (min 2)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            createLobby(lobbyName, maxPlayers)
        }

        binding.searchLobbyButton.setOnClickListener {
            val lobbyName = binding.lobbyNameEditText.text.toString().trim()
            val maxPlayers = binding.maxPlayersEditText.text.toString().trim().toIntOrNull()
            searchLobby(lobbyName, maxPlayers)
        }
    }

    private fun createLobby(name: String, maxPlayers: Int) {
        val currentUser = auth.currentUser ?: return
        val lobby = hashMapOf(
            "name" to name,
            "maxPlayers" to maxPlayers,
            "currentPlayers" to listOf(currentUser.uid),
            "status" to "open"
        )

        db.collection("lobbies")
            .add(lobby)
            .addOnSuccessListener { document ->
                Toast.makeText(requireContext(), "Lobby created successfully!", Toast.LENGTH_SHORT).show()

                // Navigate to the View Lobby page, passing the created lobby ID
                val action = LobbyFragmentDirections.actionLobbyFragmentToViewLobbyFragment(document.id)
                findNavController().navigate(action)
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to create lobby: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun searchLobby(name: String, maxPlayers: Int?) {
        val query = db.collection("lobbies")
            .whereEqualTo("name", name)
            .whereEqualTo("status", "open")

        if (maxPlayers != null) {
            query.whereLessThanOrEqualTo("maxPlayers", maxPlayers)
        }

        query.get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Toast.makeText(requireContext(), "No lobbies found", Toast.LENGTH_SHORT).show()
                } else {
                    val lobbies = documents.map { doc ->
                        LobbyItem(
                            id = doc.id,
                            name = doc.getString("name") ?: "",
                            maxPlayers = doc.getLong("maxPlayers")?.toInt() ?: 0,
                            status = doc.getString("status") ?: "unknown"
                        )
                    }

                    // Navigate to the List Lobbies page, passing the search results
                    val action = LobbyFragmentDirections.actionLobbyFragmentToListLobbiesFragment(lobbies.toTypedArray())
                    findNavController().navigate(action)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to search lobbies: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
