package com.ethereallab.chaoticbattleship.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ethereallab.chaoticbattleship.LobbyItem
import com.ethereallab.chaoticbattleship.adapters.LobbiesAdapter
import com.ethereallab.chaoticbattleship.databinding.FragmentListLobbiesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ListLobbiesFragment : Fragment() {

    private var _binding: FragmentListLobbiesBinding? = null
    private val binding get() = _binding!!
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListLobbiesBinding.inflate(inflater, container, false)

        setupListeners()
        return binding.root
    }

    private fun setupListeners() {
        binding.searchButton.setOnClickListener {
            val name = binding.searchNameInput.text.toString().trim()
            val maxPlayers = binding.searchMaxPlayersInput.text.toString().toIntOrNull()
            fetchLobbies(name, maxPlayers)
        }
    }

    private fun fetchLobbies(name: String?, maxPlayers: Int?) {
        var query = db.collection("lobbies").whereEqualTo("status", "open")

        if (!name.isNullOrEmpty()) {
            query = query.whereEqualTo("name", name)
        }
        if (maxPlayers != null) {
            query = query.whereLessThanOrEqualTo("maxPlayers", maxPlayers)
        }

        query.get()
            .addOnSuccessListener { documents ->
                val lobbies = documents.map { doc ->
                    LobbyItem(
                        id = doc.id,
                        name = doc.getString("name") ?: "",
                        maxPlayers = doc.getLong("maxPlayers")?.toInt() ?: 0,
                        status = doc.getString("status") ?: "unknown",
                        currentPlayers = doc.get("currentPlayers") as? List<String> ?: emptyList()
                    )
                }
                if (lobbies.isEmpty()) {
                    showEmptyState()
                } else {
                    setupRecyclerView(lobbies)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to fetch lobbies: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupRecyclerView(lobbies: List<LobbyItem>) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        binding.recyclerView.visibility = View.VISIBLE
        binding.emptyStateLayout.root.visibility = View.GONE
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = LobbiesAdapter(lobbies, currentUserId) { lobby, isInLobby ->
            val action = if (isInLobby) {
                ListLobbiesFragmentDirections.actionListLobbiesFragmentToViewLobbyFragment(lobby.id)
            } else {
                joinLobby(lobby.id)
                return@LobbiesAdapter
            }
            findNavController().navigate(action)
        }
    }

    private fun joinLobby(lobbyId: String) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId == null) {
            Toast.makeText(requireContext(), "You must be logged in to join a lobby.", Toast.LENGTH_SHORT).show()
            return
        }

        val lobbyRef = db.collection("lobbies").document(lobbyId)
        lobbyRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val currentPlayers = document.get("currentPlayers") as? List<String> ?: emptyList()
                    val maxPlayers = document.getLong("maxPlayers")?.toInt() ?: Int.MAX_VALUE

                    if (currentPlayers.size >= maxPlayers) {
                        Toast.makeText(requireContext(), "Lobby is full. Cannot join.", Toast.LENGTH_SHORT).show()
                        return@addOnSuccessListener
                    }

                    if (!currentPlayers.contains(currentUserId)) {
                        val updatedPlayers = currentPlayers + currentUserId
                        lobbyRef.update("currentPlayers", updatedPlayers)
                            .addOnSuccessListener {
                                findNavController().navigate(
                                    ListLobbiesFragmentDirections.actionListLobbiesFragmentToViewLobbyFragment(lobbyId)
                                )
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(requireContext(), "Failed to join lobby: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(requireContext(), "You are already in this lobby.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Lobby does not exist.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error fetching lobby: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showEmptyState() {
        binding.recyclerView.visibility = View.GONE
        binding.emptyStateLayout.root.visibility = View.VISIBLE
        binding.emptyStateLayout.backToLobbyButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
