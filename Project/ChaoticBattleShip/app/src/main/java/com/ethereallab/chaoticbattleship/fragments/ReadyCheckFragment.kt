package com.ethereallab.chaoticbattleship.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ethereallab.chaoticbattleship.adapters.PlayersAdapter
import com.ethereallab.chaoticbattleship.databinding.FragmentReadyCheckBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class ReadyCheckFragment : Fragment() {

    private var _binding: FragmentReadyCheckBinding? = null
    private val binding get() = _binding!!
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private var lobbyId: String? = null
    private var currentPlayerId: String? = null
    private var currentPlayers = listOf<String>()
    private var readyPlayers = listOf<String>()
    private var isReady = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReadyCheckBinding.inflate(inflater, container, false)
        lobbyId = arguments?.getString("lobbyId")
        currentPlayerId = auth.currentUser?.uid

        setupListeners()
        fetchLobbyDetails()

        return binding.root
    }

    private fun fetchLobbyDetails() {
        lobbyId?.let { id ->
            db.collection("lobbies").document(id).addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Toast.makeText(
                        requireContext(),
                        "Error fetching lobby: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@addSnapshotListener
                }
                snapshot?.let {
                    currentPlayers = it.get("currentPlayers") as? List<String> ?: emptyList()
                    readyPlayers = it.get("readyPlayers") as? List<String> ?: emptyList()
                    val status = it.getString("status") ?: "unknown"

                    binding.lobbyStatus.text = "Lobby Status: $status"
                    binding.readyButton.text =
                        if (readyPlayers.contains(currentPlayerId)) "Unready" else "Ready"
                    isReady = readyPlayers.contains(currentPlayerId)

                    updateUIBasedOnStatus(status)
                    setupRecyclerView()
                }
            }
        }
    }

    private fun updateUIBasedOnStatus(status: String) {
        // Hide ready button if the status is not "open"
        binding.readyButton.visibility = if (status == "open") View.VISIBLE else View.GONE

        // Show the Start button only if all players are ready and there are at least two players
        val allReady = readyPlayers.containsAll(currentPlayers) && currentPlayers.size >= 2
        binding.startButton.visibility =
            if (status == "open" && allReady) View.VISIBLE else View.GONE
    }

    private fun setupRecyclerView() {
        binding.playersRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.playersRecyclerView.adapter = PlayersAdapter(currentPlayers, readyPlayers)
    }

    private fun setupListeners() {
        binding.readyButton.setOnClickListener {
            toggleReadyStatus()
        }

        binding.startButton.setOnClickListener {
            startSession()
        }
    }

    private fun startSession() {
        lobbyId?.let { id ->
            db.collection("lobbies").document(id)
                .update("status", "place")
                .addOnSuccessListener {
                    Toast.makeText(
                        requireContext(),
                        "Session started successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        requireContext(),
                        "Failed to start session: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private fun toggleReadyStatus() {
        lobbyId?.let { id ->
            val operation = if (isReady) {
                mapOf("readyPlayers" to FieldValue.arrayRemove(currentPlayerId))
            } else {
                mapOf("readyPlayers" to FieldValue.arrayUnion(currentPlayerId))
            }
            db.collection("lobbies").document(id).update(operation)
                .addOnSuccessListener {
                    Toast.makeText(
                        requireContext(),
                        "Ready status updated successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        requireContext(),
                        "Failed to update ready status: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
