package com.ethereallab.tapitscores
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ethereallab.tapitscores.model.ScoreEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScoreDao {
    @Query("SELECT * FROM score_table ORDER BY score DESC LIMIT 10")
    fun getTopScores(): Flow<List<ScoreEntity>> // Switches to Flow for live updates

    @Insert
    suspend fun insertScore(score: ScoreEntity)
}