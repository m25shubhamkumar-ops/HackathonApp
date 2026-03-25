package com.hackathon.models

import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class User(
    val id: String,
    val username: String,
    val email: String,
    val leetcodeRating: Int = 0,
    val codeforcesRating: Int = 0,
    val dailyGoal: Int = 5,
    val problemsSolvedToday: Int = 0,
    val streakDays: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Serializable
data class UserProfile(
    val id: String,
    val username: String,
    val email: String,
    val avatarUrl: String? = null,
    val bio: String? = null,
    val leetcodeUsername: String? = null,
    val codeforcesUsername: String? = null,
    val totalProblems: Int = 0,
    val totalContests: Int = 0
)

@Serializable
data class UserProgress(
    val userId: String,
    val dailyGoal: Int,
    val problemsSolvedToday: Int,
    val streakDays: Int,
    val lastActiveDate: Long,
    val totalProblemsThisMonth: Int
)