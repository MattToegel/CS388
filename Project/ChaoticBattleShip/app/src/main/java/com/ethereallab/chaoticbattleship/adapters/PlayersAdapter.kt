package com.ethereallab.chaoticbattleship.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PlayersAdapter(
    private val players: List<String>,
    private val readyPlayers: List<String>
) : RecyclerView.Adapter<PlayersAdapter.PlayerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_2, parent, false)
        return PlayerViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        val player = players[position]
        holder.bind("Player ${position + 1}", readyPlayers.contains(player))
    }

    override fun getItemCount() = players.size

    class PlayerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(android.R.id.text1)
        private val statusTextView: TextView = itemView.findViewById(android.R.id.text2)

        fun bind(playerName: String, isReady: Boolean) {
            nameTextView.text = playerName
            statusTextView.text = if (isReady) "Ready" else "Not Ready"
        }
    }
}
