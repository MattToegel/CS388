package com.ethereallab.tapitscores

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import com.ethereallab.tapitscores.databinding.ActivityGameBinding

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding
    private lateinit var countDownTimer: CountDownTimer
    private var timeLeftInMillis: Long = 30000 // 30 seconds
    private var score: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Using View Binding
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startTimer()

        binding.btnTap.setOnClickListener {
            score++
            binding.tvScore.text = "Score: $score"
        }
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                val secondsLeft = millisUntilFinished / 1000
                binding.tvTimer.text = "Time: ${secondsLeft}s"
            }

            override fun onFinish() {
                binding.tvTimer.text = "Time's Up!"
                navigateToScoreEntry()
            }
        }.start()
    }

    private fun navigateToScoreEntry() {
        val intent = Intent(this, ScoreEntryActivity::class.java)
        intent.putExtra("FINAL_SCORE", score)
        startActivity(intent)
        finish() // Close GameActivity
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer.cancel()
    }
}
