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
import com.ethereallab.chaoticbattleship.databinding.FragmentUserLobbiesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserLobbiesFragment : Fragment() {

    private var _binding: FragmentUserLobbiesBinding? = null
    private val binding get() = _binding!!
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserLobbiesBinding.inflate(inflater, container, false)

        fetchUserLobbies()
        return binding.root
    }

    private fun fetchUserLobbies() {
        val currentUserId = auth.currentUser?.uid
        if (currentUserId == null) {
            Toast.makeText(requireContext(), "You must be logged in.", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("lobbies")
            .whereArrayContains("currentPlayers", currentUserId)
            .whereNotEqualTo("status", "completed")
            .get()
            .addOnSuccessListener { documents ->
                val lobbies = documents.map { document ->
                    LobbyItem(
                        id = document.id,
                        name = document.getString("name").orEmpty(),
                        status = document.getString("status").orEmpty(),
                        maxPlayers = document.getLong("maxPlayers")?.toInt() ?: 0,
                        currentPlayers = document.get("currentPlayers") as? List<String> ?: emptyList()
                    )
                }
                setupRecyclerView(lobbies)
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error fetching lobbies: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun setupRecyclerView(lobbies: List<LobbyItem>) {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = LobbiesAdapter(lobbies, auth.currentUser?.uid ?: "") { lobby, _ ->
                if (lobby.status == "open") {
                    // Navigate to ViewLobbyFragment if status is "open"
                    val action = UserLobbiesFragmentDirections.actionUserLobbiesFragmentToViewLobbyFragment(lobby.id)
                    findNavController().navigate(action)
                } else {
                    // Navigate to GameGridFragment if status is not "open"
                    val action = UserLobbiesFragmentDirections.actionUserLobbiesFragmentToGameGridFragment(
                        lobbyId = lobby.id
                    )
                    findNavController().navigate(action)
                }
            }
        }
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
