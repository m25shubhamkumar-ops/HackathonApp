// Analysis.kt

package com.hackathon.models

// Data class for User Analytics
data class UserAnalytics(
    val userId: String,
    val actions: List<String>,
    val duration: Int
)

// Data class for daily statistics
data class DailyStats(
    val date: String,
    val userCount: Int,
    val activityCount: Int
)

// Data class for weekly report
data class WeeklyReport(
    val weekNumber: Int,
    val dailyStats: List<DailyStats>
)

// Data class for skill analysis
data class SkillAnalysis(
    val skillName: String,
    val proficiencyLevel: Int,
    val improvementSuggestions: List<String>
)