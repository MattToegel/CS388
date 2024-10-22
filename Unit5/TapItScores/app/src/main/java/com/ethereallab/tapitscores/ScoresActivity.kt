package com.ethereallab.tapitscores

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ethereallab.tapitscores.adapter.ScoreAdapter
import com.ethereallab.tapitscores.databinding.ActivityScoresBinding
import kotlinx.coroutines.launch

class ScoresActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScoresBinding
    private lateinit var scoreAdapter: ScoreAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Using View Binding
        binding = ActivityScoresBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val scoreDao = (application as TapGameApplication).database.scoreDao()

        lifecycleScope.launch {
            scoreDao.getTopScores().collect { topScores ->
                scoreAdapter = ScoreAdapter(topScores)
                binding.rvScores.adapter = scoreAdapter
                binding.rvScores.layoutManager = LinearLayoutManager(this@ScoresActivity)
            }
        }

    }
}
