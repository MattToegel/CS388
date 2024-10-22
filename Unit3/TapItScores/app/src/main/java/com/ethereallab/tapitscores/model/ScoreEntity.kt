package com.ethereallab.tapitscores.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "score_table")
data class ScoreEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "initials") val initials: String,
    @ColumnInfo(name = "score") val score: Int
)
