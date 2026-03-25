package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.data.local.PracticeSession
import com.example.myapplication.getAppColors
import com.example.myapplication.ui.viewmodels.AnalysisViewModel

data class AnalysisSession(
    val title: String,
    val duration: String,
    val date: String,
    val platform: String,
    val color: Color
)

private fun platformColor(platform: String): Color = when (platform.uppercase()) {
    "LC" -> Color(0xFFC8F7C5)
    "CF" -> Color(0xFFD8C2FF)
    "CC" -> Color(0xFFFFC4C4)
    "HR" -> Color(0xFFB5E3FF)
    else -> Color(0xFFF1F1F1)
}

private fun PracticeSession.toAnalysisSession() = AnalysisSession(
    title = "$platform - $topic",
    duration = duration,
    date = date,
    platform = platform.take(2).uppercase(),
    color = platformColor(platform.take(2).uppercase())
)

private val mockSessions = listOf(
    AnalysisSession("LeetCode - Arrays", "45m", "12 Oct 2023", "LC", Color(0xFFC8F7C5)),
    AnalysisSession("GeeksforGeeks - Trees", "60m", "13 Oct 2023", "GFG", Color(0xFFFFC4C4)),
    AnalysisSession("Codeforces - DP", "50m", "14 Oct 2023", "CF", Color(0xFFD8C2FF)),
    AnalysisSession("LeetCode - Graphs", "40m", "15 Oct 2023", "LC", Color(0xFFB5E3FF)),
    AnalysisSession("Codeforces - Greedy", "55m", "16 Oct 2023", "CF", Color(0xFFF1F1F1)),
    AnalysisSession("GeeksforGeeks - Stack", "35m", "17 Oct 2023", "GFG", Color(0xFFDFFFE0))
)

@Composable
fun AnalysisScreen(
    isDarkTheme: Boolean,
    isOledMode: Boolean,
    accentColor: Color,
    analysisViewModel: AnalysisViewModel = viewModel()
) {
    val colors = getAppColors(isDarkTheme, isOledMode)
    val bg = colors["bg"]!!
    val textC = colors["text"]!!
    val secText = colors["secondaryText"]!!

    val roomSessions by analysisViewModel.sessions.collectAsState()
    val sessions = if (roomSessions.isEmpty()) {
        mockSessions
    } else {
        roomSessions.map { it.toAnalysisSession() }
    }

    var selected by remember { mutableStateOf<AnalysisSession?>(null) }

    if (selected == null) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(bg)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Text(
                    "Analysis Sessions",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = textC,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            item {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Session Platform", fontWeight = FontWeight.Bold, color = secText)
                    Text("Duration", fontWeight = FontWeight.Bold, color = secText)
                    Text("Date", fontWeight = FontWeight.Bold, color = secText)
                }
            }
            items(sessions) { s ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selected = s },
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDarkTheme) s.color.copy(alpha = 0.15f) else s.color
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1.2f)) {
                            Text(s.title, fontWeight = FontWeight.Bold, color = textC)
                            Text("Problem ID", fontSize = 12.sp, color = secText)
                        }
                        Text(s.duration, modifier = Modifier.weight(0.4f), color = textC)
                        Text(s.date, modifier = Modifier.weight(0.8f), color = textC)
                    }
                }
            }
        }
    } else {
        SessionDetailScreen(
            session = selected!!,
            isDarkTheme = isDarkTheme,
            isOledMode = isOledMode,
            accentColor = accentColor,
            onBack = { selected = null }
        )
    }
}

@Composable
fun SessionDetailScreen(
    session: AnalysisSession,
    isDarkTheme: Boolean,
    isOledMode: Boolean,
    accentColor: Color,
    onBack: () -> Unit
) {
    val colors = getAppColors(isDarkTheme, isOledMode)
    val bg = colors["bg"]!!
    val surface = colors["surface"]!!
    val textC = colors["text"]!!
    val secText = colors["secondaryText"]!!

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = textC,
                    modifier = Modifier.clickable { onBack() }
                )
                Spacer(Modifier.width(8.dp))
                Text("Session Details", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textC)
            }
        }
        item {
            Spacer(Modifier.height(8.dp))
            Text(session.title, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = textC)
            Text("Duration: ${session.duration}", color = secText)
            Text("Date: ${session.date}", color = secText)
            Spacer(Modifier.height(8.dp))
        }

        val detailMap = listOf(
            "Questions Solved" to "Two Sum, Binary Search, DP",
            "Time Taken" to session.duration,
            "Logic Used" to "Prefix sum + greedy + two pointers",
            "Good Approach" to "Optimized with O(n) solution"
        )
        items(detailMap) { (title, value) ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = surface)
            ) {
                Column(Modifier.padding(12.dp)) {
                    Text(title, fontWeight = FontWeight.Bold, color = textC)
                    Text(value, color = secText)
                }
            }
        }

        item {
            Spacer(Modifier.height(16.dp))
            Text("PDFs", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = textC)
        }

        val pdfs = listOf("Your Code PDF", "Wrong vs Right PDF", "Correct Solution PDF")
        items(pdfs) { title ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(surface, RoundedCornerShape(10.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Info, contentDescription = null, tint = accentColor)
                Spacer(Modifier.width(10.dp))
                Text(title, modifier = Modifier.weight(1f), color = textC)
                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accentColor,
                        contentColor = Color.White
                    )
                ) {
                    Text("View")
                }
            }
        }
        item { Spacer(Modifier.height(20.dp)) }
    }
}
