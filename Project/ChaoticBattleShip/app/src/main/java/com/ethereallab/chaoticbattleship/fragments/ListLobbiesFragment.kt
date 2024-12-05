package com.ethereallab.chaoticbattleship.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ethereallab.chaoticbattleship.LobbyItem
import com.ethereallab.chaoticbattleship.R

class ListLobbiesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val lobbies = arguments?.getParcelableArray("lobbies")?.toList() as? List<LobbyItem>
        // Inflate the layout and display the list of lobbies with join/view buttons
        return inflater.inflate(R.layout.fragment_list_lobbies, container, false)
    }
}
