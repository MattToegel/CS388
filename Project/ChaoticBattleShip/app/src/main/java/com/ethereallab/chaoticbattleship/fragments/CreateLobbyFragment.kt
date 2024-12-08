package com.ethereallab.chaoticbattleship.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ethereallab.chaoticbattleship.databinding.FragmentCreateLobbyBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CreateLobbyFragment : Fragment() {

    private var _binding: FragmentCreateLobbyBinding? = null
    private val binding get() = _binding!!
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateLobbyBinding.inflate(inflater, container, false)

        binding.createLobbyButton.setOnClickListener { createLobby() }

        return binding.root
    }

    private fun createLobby() {
        val lobbyName = binding.lobbyNameEditText.text.toString().trim()
        val maxPlayers = binding.maxPlayersEditText.text.toString().trim().toIntOrNull()

        if (lobbyName.isEmpty() || maxPlayers == null || maxPlayers < 2) {
            Toast.makeText(requireContext(), "Enter a valid lobby name and max players (min 2)", Toast.LENGTH_SHORT).show()
            return
        }

        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(requireContext(), "You must be logged in to create a lobby", Toast.LENGTH_SHORT).show()
            return
        }

        val lobby = hashMapOf(
            "name" to lobbyName,
            "maxPlayers" to maxPlayers,
            "currentPlayers" to listOf(currentUser.uid),
            "status" to "open"
        )

        FirebaseFirestore.getInstance().collection("lobbies")
            .add(lobby)
            .addOnSuccessListener { document ->
                Toast.makeText(requireContext(), "Lobby created successfully!", Toast.LENGTH_SHORT).show()
                val action = CreateLobbyFragmentDirections.actionCreateLobbyFragmentToViewLobbyFragment(document.id)
                findNavController().navigate(action)
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to create lobby: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
