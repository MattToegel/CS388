package com.ethereallab.chaoticbattleship.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ethereallab.chaoticbattleship.R
import com.ethereallab.chaoticbattleship.databinding.FragmentLobbyBinding

class LobbyFragment : Fragment() {

    private var _binding: FragmentLobbyBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLobbyBinding.inflate(inflater, container, false)

        setupListeners()
        return binding.root
    }

    private fun setupListeners() {
        binding.createLobbyButton.setOnClickListener {
            findNavController().navigate(R.id.action_lobbyFragment_to_createLobbyFragment)
        }
        binding.searchLobbyButton.setOnClickListener {
            findNavController().navigate(R.id.action_lobbyFragment_to_listLobbiesFragment)
        }
        binding.userLobbiesButton.setOnClickListener {
            findNavController().navigate(R.id.action_lobbyFragment_to_userLobbiesFragment)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
