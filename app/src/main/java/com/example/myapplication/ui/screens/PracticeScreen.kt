package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.getAppColors

data class Platform(
    val name: String,
    val url: String,
    val softColor: Color,
    val darkColor: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeScreen(
    isDarkTheme: Boolean,
    isOledMode: Boolean,
    accentColor: Color,
    onOpenArena: (String) -> Unit
) {
    val colors = getAppColors(isDarkTheme, isOledMode)
    val surface = colors["surface"]!!
    val textC = colors["text"]!!
    val secText = colors["secondaryText"]!!

    val platforms = listOf(
        Platform("LeetCode", "https://leetcode.com/problemset/all/", Color(0xFFFEF3C7), Color(0xFFD97706)),
        Platform("HackerRank", "https://www.hackerrank.com/domains", Color(0xFFD1FAE5), Color(0xFF059669)),
        Platform("Codeforces", "https://codeforces.com/problemset", Color(0xFFDBEAFE), Color(0xFF2563EB)),
        Platform("CodeChef", "https://www.codechef.com/practice", Color(0xFFFFEDD5), Color(0xFFEA580C))
    )

    var difficulty by remember { mutableStateOf("Medium") }
    var duration by remember { mutableStateOf("45 mins") }
    var questions by remember { mutableStateOf("3") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        Text(
            "Practice Arena",
            color = textC,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text("Configure a sample test or choose a platform.", color = secText, fontSize = 15.sp)
        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = surface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    "Generate Sample Test",
                    fontWeight = FontWeight.Bold,
                    color = textC,
                    fontSize = 16.sp
                )
                Spacer(Modifier.height(12.dp))

                PracticeDropdown(
                    label = "Difficulty",
                    options = listOf("Easy", "Medium", "Hard"),
                    selected = difficulty,
                    onSelectionChange = { difficulty = it }
                )
                Spacer(Modifier.height(8.dp))
                PracticeDropdown(
                    label = "Duration",
                    options = listOf("15 mins", "30 mins", "45 mins", "60 mins"),
                    selected = duration,
                    onSelectionChange = { duration = it }
                )
                Spacer(Modifier.height(8.dp))
                PracticeDropdown(
                    label = "Questions",
                    options = listOf("1", "2", "3", "5"),
                    selected = questions,
                    onSelectionChange = { questions = it }
                )

                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = { /* TODO: launch mock test */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                ) {
                    Text("START TEST", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Or jump into a platform:", color = textC, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            items(platforms) { platform ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clickable { onOpenArena(platform.url) },
                    colors = CardDefaults.cardColors(containerColor = surface),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(
                                    if (isDarkTheme) platform.darkColor.copy(alpha = 0.2f) else platform.softColor,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                platform.name.first().toString(),
                                color = if (isDarkTheme) platform.softColor else platform.darkColor,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(platform.name, color = textC, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeDropdown(
    label: String,
    options: List<String>,
    selected: String,
    onSelectionChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelectionChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
