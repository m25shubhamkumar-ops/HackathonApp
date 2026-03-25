data class Contest(
    val id: String,
    val name: String,
    val description: String,
    val startDate: String,
    val endDate: String
)

data class ContestResponse(
    val contest: Contest,
    val participants: List<String>
)

data class UserContestHistory(
    val userId: String,
    val contestId: String,
    val status: String,
    val score: Int,
    val submissionDate: String
)