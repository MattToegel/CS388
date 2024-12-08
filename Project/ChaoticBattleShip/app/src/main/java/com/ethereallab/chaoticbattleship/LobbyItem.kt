package com.ethereallab.chaoticbattleship

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LobbyItem(
    val id: String,
    val name: String,
    val maxPlayers: Int,
    val status: String,
    val currentPlayers: List<String>
) : Parcelable
