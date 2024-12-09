package com.ethereallab.chaoticbattleship.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ethereallab.chaoticbattleship.databinding.FragmentViewLobbyBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ViewLobbyFragment : Fragment() {

    private var _binding: FragmentViewLobbyBinding? = null
    private val binding get() = _binding!!
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViewLobbyBinding.inflate(inflater, container, false)

        val lobbyId = arguments?.getString("lobbyId") ?: return binding.root

        fetchLobbyDetails(lobbyId)

        binding.leaveLobbyButton.setOnClickListener {
            leaveLobby(lobbyId)
        }
        binding.readyCheckButton.setOnClickListener {
            lobbyId?.let { id ->
                val action = ViewLobbyFragmentDirections.actionViewLobbyFragmentToReadyCheckFragment(id)
                findNavController().navigate(action)
            }
        }

        return binding.root
    }

    private fun fetchLobbyDetails(lobbyId: String) {
        db.collection("lobbies").document(lobbyId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    binding.lobbyName.text = document.getString("name")
                    binding.currentPlayers.text =
                        "Players: ${(document.get("currentPlayers") as? List<*>)?.size}"
                    binding.maxPlayers.text = "Max Players: ${document.getLong("maxPlayers")}"
                } else {
                    Toast.makeText(requireContext(), "Lobby not found.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to fetch lobby: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun leaveLobby(lobbyId: String) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId == null) {
            Toast.makeText(requireContext(), "You must be logged in to leave the lobby.", Toast.LENGTH_SHORT).show()
            return
        }

        val lobbyRef = db.collection("lobbies").document(lobbyId)
        lobbyRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val currentPlayers = document.get("currentPlayers") as? List<String> ?: emptyList()
                    val updatedPlayers = currentPlayers.filter { it != currentUserId }

                    if (updatedPlayers.isEmpty()) {
                        // Delete the lobby
                        lobbyRef.delete()
                            .addOnSuccessListener {
                                navigateToLobbyFragment() // Navigate to LobbyFragment after successful deletion
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(requireContext(), "Failed to delete lobby: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        // Update currentPlayers list
                        lobbyRef.update("currentPlayers", updatedPlayers)
                            .addOnSuccessListener {
                                navigateToLobbyFragment() // Navigate to LobbyFragment after successful update
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(requireContext(), "Failed to leave lobby: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    Toast.makeText(requireContext(), "Lobby does not exist.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error fetching lobby: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun navigateToLobbyFragment() {
        if (isAdded) { // Ensure fragment is attached
            val action = ViewLobbyFragmentDirections.actionViewLobbyFragmentToLobbyFragment()
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
