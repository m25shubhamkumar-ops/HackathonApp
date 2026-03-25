package com.example.myapplication.ui.screens

import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.getAppColors
import com.example.myapplication.ui.viewmodels.ContestViewModel

data class UpcomingContest(
    val name: String,
    val badge: String,
    val time: String,
    val duration: String,
    val url: String
)

data class ContestItem(
    val title: String,
    val badge: String,
    val time: String,
    val date: String,
    val tier: String,
    val bgColor: Color,
    val url: String
)

@Composable
fun ContestScreen(
    isDarkTheme: Boolean,
    isOledMode: Boolean,
    accentColor: Color,
    onOpenArena: (String) -> Unit,
    contestViewModel: ContestViewModel = viewModel()
) {
    val colors = getAppColors(isDarkTheme, isOledMode)
    val context = LocalContext.current
    val bg = colors["bg"]!!
    val surface = colors["surface"]!!
    val textC = colors["text"]!!
    val secText = colors["secondaryText"]!!

    val upcoming = remember {
        listOf(
            UpcomingContest("LeetCode", "LC", "Oct 15, 18:00 UTC", "Duration", "https://leetcode.com"),
            UpcomingContest("Codeforces", "CF", "Oct 15, 18:00 UTC", "Duration", "https://codeforces.com"),
            UpcomingContest("CodeChef", "CC", "Oct 15, 18:00 UTC", "Duration", "https://www.codechef.com"),
            UpcomingContest("HackerRank", "HR", "Oct 15, 18:00 UTC", "Duration", "https://www.hackerrank.com"),
            UpcomingContest("GeeksforGeeks", "GFG", "Oct 15, 18:00 UTC", "Duration", "https://www.geeksforgeeks.org")
        )
    }

    val contests = remember {
        listOf(
            ContestItem("Codeforces Round", "CF", "Sat, 8 PM", "Feb 08", "Div 2", Color(0xFFFFC1D9), "https://codeforces.com"),
            ContestItem("CodeChef Starters", "CC", "Wed, 8 PM", "Feb 12", "Div 1/2", Color(0xFFC8F7C5), "https://www.codechef.com"),
            ContestItem("LeetCode Weekly", "LC", "Sun, 9 AM", "Feb 09", "Open", Color(0xFFD2C2FF), "https://leetcode.com"),
            ContestItem("AtCoder Beginner", "AC", "Sat, 6 PM", "Feb 15", "Div 2", Color(0xFFFFC1D9), "https://atcoder.jp")
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // 1. Upcoming Contests
        item {
            Text(
                "Upcoming Contests",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = textC,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        items(upcoming) { c ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenArena(c.url) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = surface)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(colors["divider"]!!),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(c.badge, fontWeight = FontWeight.Bold, color = textC)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(c.name, fontWeight = FontWeight.Bold, color = textC)
                        Text(c.time, fontSize = 12.sp, color = secText)
                    }
                    IconButton(
                        onClick = { setCalendarReminder(context, c.name, c.time) }
                    ) {
                        Icon(
                            Icons.Filled.Notifications,
                            contentDescription = "Set Reminder",
                            tint = accentColor
                        )
                    }
                }
            }
        }

        // 2. Daily Activity Grid
        item {
            Spacer(Modifier.height(16.dp))
            Text(
                "Daily Activity",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = textC
            )
        }
        item {
            val levels = listOf(0, 1, 2, 3, 4, 2, 1, 0, 0, 1, 3, 4, 4, 2, 1, 0, 2, 3, 1, 0, 0, 1, 2, 4, 3, 2, 1, 0)
            Card(
                Modifier.fillMaxWidth(),
                RoundedCornerShape(12.dp),
                CardDefaults.cardColors(containerColor = surface)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Last 4 Weeks", fontWeight = FontWeight.Bold, color = textC)
                    Spacer(Modifier.height(12.dp))
                    levels.chunked(7).forEach { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            row.forEach { lvl ->
                                val cellColor = when (lvl) {
                                    0 -> colors["divider"]!!
                                    1 -> Color(0xFFD8F5D8)
                                    2 -> Color(0xFFA8E6A8)
                                    3 -> Color(0xFF6EDC6E)
                                    else -> Color(0xFF2E8B2E)
                                }
                                Box(
                                    Modifier
                                        .size(14.dp)
                                        .background(cellColor, RoundedCornerShape(3.dp))
                                )
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                    }
                }
            }
        }

        // 3. Major Coding Contests
        item {
            Spacer(Modifier.height(16.dp))
            Text(
                "Major Coding Contests",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = textC
            )
        }
        items(contests.chunked(2)) { rowItems ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                rowItems.forEach { contest ->
                    val infinite = rememberInfiniteTransition(label = "pulse")
                    val scale by infinite.animateFloat(
                        initialValue = 0.98f,
                        targetValue = 1.0f,
                        animationSpec = infiniteRepeatable(
                            tween(1200),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "scale"
                    )
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(130.dp)
                            .graphicsLayer(scaleX = scale, scaleY = scale)
                            .clickable { onOpenArena(contest.url) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isDarkTheme) contest.bgColor.copy(alpha = 0.15f) else contest.bgColor
                        ),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isDarkTheme) contest.bgColor else Color.White
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        contest.badge,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDarkTheme) textC else Color.Black
                                    )
                                }
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    contest.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = textC,
                                    maxLines = 1
                                )
                            }
                            Spacer(Modifier.height(6.dp))
                            Text("${contest.date} • ${contest.time}", fontSize = 12.sp, color = textC)
                            Spacer(Modifier.height(4.dp))
                            Text("Tier: ${contest.tier}", fontSize = 11.sp, color = textC)
                        }
                    }
                }
                if (rowItems.size == 1) Spacer(Modifier.weight(1f))
            }
        }
        item { Spacer(Modifier.height(20.dp)) }
    }
}

fun setCalendarReminder(context: Context, title: String, timeDesc: String) {
    val intent = Intent(Intent.ACTION_INSERT).apply {
        data = CalendarContract.Events.CONTENT_URI
        putExtra(CalendarContract.Events.TITLE, title)
        putExtra(
            CalendarContract.Events.DESCRIPTION,
            "Upcoming coding contest: $title at $timeDesc"
        )
    }
    try {
        context.startActivity(intent)
    } catch (_: Exception) { }
}
