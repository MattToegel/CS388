package com.ethereallab.chaoticbattleship.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ethereallab.chaoticbattleship.LobbyItem
import com.ethereallab.chaoticbattleship.R

class LobbiesAdapter(
    private val lobbies: List<LobbyItem>,
    private val currentUserId: String,
    private val onLobbyAction: (LobbyItem, Boolean) -> Unit
) : RecyclerView.Adapter<LobbiesAdapter.LobbyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LobbyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_lobby, parent, false)
        return LobbyViewHolder(view)
    }

    override fun onBindViewHolder(holder: LobbyViewHolder, position: Int) {
        val lobby = lobbies[position]
        val isInLobby = lobby.currentPlayers.contains(currentUserId) // Check if user is in lobby
        holder.bind(lobby, isInLobby, onLobbyAction)
    }

    override fun getItemCount() = lobbies.size

    class LobbyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val lobbyName = itemView.findViewById<TextView>(R.id.lobby_name)
        private val maxPlayers = itemView.findViewById<TextView>(R.id.lobby_max_players)
        private val actionButton = itemView.findViewById<Button>(R.id.lobby_join_button)

        fun bind(lobby: LobbyItem, isInLobby: Boolean, onLobbyAction: (LobbyItem, Boolean) -> Unit) {
            lobbyName.text = lobby.name
            maxPlayers.text = "Max Players: ${lobby.maxPlayers}"
            actionButton.text = if (isInLobby) "View" else "Join"

            actionButton.setOnClickListener {
                onLobbyAction(lobby, isInLobby)
            }
        }
    }
}
