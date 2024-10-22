package com.ethereallab.tapitscores.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ethereallab.tapitscores.databinding.ItemScoreBinding
import com.ethereallab.tapitscores.model.ScoreEntity

class ScoreAdapter(private val scores: List<ScoreEntity>) :
    RecyclerView.Adapter<ScoreAdapter.ScoreViewHolder>() {

    inner class ScoreViewHolder(private val binding: ItemScoreBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(score: ScoreEntity) {
            binding.tvInitials.text = score.initials
            binding.tvScore.text = score.score.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreViewHolder {
        val binding = ItemScoreBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ScoreViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScoreViewHolder, position: Int) {
        val score = scores[position]
        holder.bind(score)
    }

    override fun getItemCount() = scores.size
}