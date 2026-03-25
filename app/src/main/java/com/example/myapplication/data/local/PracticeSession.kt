package com.example.myapplication.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "practice_sessions")
data class PracticeSession(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val platform: String,
    val topic: String,
    val difficulty: String,
    val duration: String,
    val questionCount: Int,
    val date: String
)
