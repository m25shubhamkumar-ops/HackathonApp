package com.example.myapplication.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.getAppColors

@Composable
fun HomeScreen(
    isDarkTheme: Boolean,
    isOledMode: Boolean,
    accentColor: Color,
    onThemeToggle: () -> Unit,
    onOpenProfile: () -> Unit,
    onEnterArenaClick: () -> Unit
) {
    val context = LocalContext.current
    val colors = getAppColors(isDarkTheme, isOledMode)
    val surface = colors["surface"]!!
    val textC = colors["text"]!!
    val secText = colors["secondaryText"]!!
    val divColor = colors["divider"]!!

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Hello, Ujjwal! 👋",
                    color = textC,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Current Rating: 1542 | LeetCode",
                    color = secText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Card(
                modifier = Modifier.clickable { onThemeToggle() },
                colors = CardDefaults.cardColors(containerColor = surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    if (isDarkTheme) "☀️" else "🌙",
                    color = accentColor,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(10.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(surface)
                    .clickable { onOpenProfile() }
                    .border(2.dp, accentColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = "Profile", tint = secText)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        Text("Upcoming Contests", color = textC, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = surface),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    "LeetCode Weekly 385",
                    color = textC,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Difficulty: Medium - Hard", color = secText, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).background(accentColor, CircleShape))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Starts in: 02:14:00",
                        color = accentColor,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://leetcode.com/contest/")
                        )
                        try {
                            context.startActivity(intent)
                        } catch (_: Exception) { }
                        onEnterArenaClick()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accentColor,
                        contentColor = Color.White
                    )
                ) {
                    Text("Enter Arena", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        Text("Your Progress", color = textC, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = surface),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Daily Goal",
                        color = textC,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "3/5 Problems",
                        color = accentColor,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                LinearProgressIndicator(
                    progress = { 0.6f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = accentColor,
                    trackColor = divColor,
                    strokeCap = StrokeCap.Round
                )
                Spacer(modifier = Modifier.height(24.dp))
                // Rating trend chart placeholder
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .drawBehind {
                            drawRoundRect(
                                color = divColor,
                                style = Stroke(
                                    width = 2.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(
                                        floatArrayOf(20f, 10f),
                                        0f
                                    )
                                ),
                                cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx())
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Rating Trend Chart Placeholder",
                        color = secText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}
