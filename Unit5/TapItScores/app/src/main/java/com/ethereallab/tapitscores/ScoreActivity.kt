package com.ethereallab.tapitscores

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ethereallab.tapitscores.databinding.ActivityScoreEntryBinding
import com.ethereallab.tapitscores.model.ScoreEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ScoreEntryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScoreEntryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Using View Binding
        binding = ActivityScoreEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val finalScore = intent.getIntExtra("FINAL_SCORE", 0)
        binding.tvFinalScore.text = "Your Score: $finalScore"

        binding.btnSubmitScore.setOnClickListener {
            val initials = binding.etInitials.text.toString()
            if (initials.isNotEmpty()) {
                val scoreEntity = ScoreEntity(
                    initials = initials,
                    score = finalScore
                )
                val scoreDao = (application as TapGameApplication).database.scoreDao()

                // Use Coroutine to perform database operations
                CoroutineScope(Dispatchers.IO).launch {
                    scoreDao.insertScore(scoreEntity)
                    runOnUiThread {
                        val intent = Intent(this@ScoreEntryActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
                // suggested alternative
                /*lifecycleScope.launch(Dispatchers.IO) {
                    scoreDao.insertScore(scoreEntity)
                    withContext(Dispatchers.Main) {
                        val intent = Intent(this@ScoreEntryActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }*/
            } else {
                Toast.makeText(this, "Please enter your initials", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
