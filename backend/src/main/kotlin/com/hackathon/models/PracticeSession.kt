package com.hackathon.models

data class PracticeSession(
    val id: Int,
    val duration: Int,
    val problems: List<Problem>
)

data class Problem(
    val name: String,
    val difficulty: String,
    val solved: Boolean
)

data class SessionStats(
    val totalTime: Int,
    val problemsSolved: Int,
    val accuracy: Double
)