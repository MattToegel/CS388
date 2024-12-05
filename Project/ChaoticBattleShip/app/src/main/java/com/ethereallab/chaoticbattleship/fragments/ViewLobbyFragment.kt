package com.ethereallab.chaoticbattleship.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ethereallab.chaoticbattleship.R

class ViewLobbyFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val lobbyId = arguments?.getString("lobbyId") ?: "Unknown Lobby"
        // Inflate the layout and display lobby details
        return inflater.inflate(R.layout.fragment_view_lobby, container, false)
    }
}
