package com.example.myapplication

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                // --- GLOBAL APP STATE ---
                var isLoggedIn by remember { mutableStateOf(false) }
                var currentTab by remember { mutableStateOf("Home") }
                var isArenaOpen by remember { mutableStateOf(false) }
                var isProfileOpen by remember { mutableStateOf(false) }
                var isSettingsOpen by remember { mutableStateOf(false) }
                var arenaUrl by remember { mutableStateOf("https://leetcode.com/problemset/all/") }

                // Theme Engine States
                var isDarkTheme by remember { mutableStateOf(true) }
                var isOledMode by remember { mutableStateOf(false) }
                var accentColor by remember { mutableStateOf(Color(0xFF2563EB)) }

                // --- MASTER NAVIGATION CONTROLLER ---
                if (!isLoggedIn) {
                    AuthScreen(isDarkTheme, isOledMode, accentColor) { isLoggedIn = true }
                } else if (isSettingsOpen) {
                    SettingsScreen(
                        isDarkTheme, isOledMode, accentColor,
                        onBackPressed = { isSettingsOpen = false },
                        onToggleDarkTheme = { isDarkTheme = !isDarkTheme },
                        onToggleOledMode = { isOledMode = !isOledMode },
                        onSelectAccent = { accentColor = it },
                        onSignOut = { isLoggedIn = false; isSettingsOpen = false; isProfileOpen = false; currentTab = "Home" }
                    )
                } else if (isProfileOpen) {
                    ProfileScreen(isDarkTheme, isOledMode, accentColor, onBackPressed = { isProfileOpen = false }) { isSettingsOpen = true }
                } else if (isArenaOpen) {
                    ArenaScreen(arenaUrl, accentColor) { isArenaOpen = false }
                } else {
                    MainScaffold(
                        currentTab, isDarkTheme, isOledMode, accentColor,
                        onTabSelected = { currentTab = it },
                        onThemeToggle = { isDarkTheme = !isDarkTheme },
                        onOpenArena = { url -> arenaUrl = url; isArenaOpen = true },
                        onOpenProfile = { isProfileOpen = true }
                    )
                }
            }
        }
    }
}

@Composable
fun getAppColors(isDarkTheme: Boolean, isOledMode: Boolean): Map<String, Color> {
    return mapOf(
        "bg" to if (!isDarkTheme) Color(0xFFF8FAFC) else if (isOledMode) Color(0xFF000000) else Color(0xFF0F172A),
        "surface" to if (!isDarkTheme) Color(0xFFFFFFFF) else if (isOledMode) Color(0xFF09090B) else Color(0xFF1E293B),
        "text" to if (!isDarkTheme) Color(0xFF0F172A) else Color(0xFFF8FAFC),
        "secondaryText" to if (!isDarkTheme) Color(0xFF64748B) else Color(0xFF94A3B8),
        "divider" to if (!isDarkTheme) Color(0xFFE2E8F0) else if (isOledMode) Color(0xFF27272A) else Color(0xFF334155)
    )
}

@Composable
fun MainScaffold(currentTab: String, isDarkTheme: Boolean, isOledMode: Boolean, accentColor: Color, onTabSelected: (String) -> Unit, onThemeToggle: () -> Unit, onOpenArena: (String) -> Unit, onOpenProfile: () -> Unit) {
    val colors = getAppColors(isDarkTheme, isOledMode)
    val bg = colors["bg"]!!
    val surface = colors["surface"]!!
    val secText = colors["secondaryText"]!!

    Scaffold(
        containerColor = bg,
        bottomBar = {
            NavigationBar(containerColor = surface, tonalElevation = 8.dp) {
                listOf(
                    Triple("Home", Icons.Default.Home, "Home"),
                    Triple("Contest", Icons.Default.Star, "Contest"),
                    Triple("Practice", Icons.Default.PlayArrow, "Practice"),
                    Triple("Analysis", Icons.Default.Settings, "Analysis")
                ).forEach { (title, icon, tabId) ->
                    NavigationBarItem(
                        selected = currentTab == tabId, onClick = { onTabSelected(tabId) }, icon = { Icon(icon, contentDescription = title) }, label = { Text(title, fontWeight = FontWeight.Medium) },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = accentColor, selectedTextColor = accentColor, indicatorColor = accentColor.copy(alpha = 0.15f), unselectedIconColor = secText, unselectedTextColor = secText)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (currentTab) {
                "Home" -> HomeContent(isDarkTheme, isOledMode, accentColor, onThemeToggle, onOpenProfile) { onOpenArena("https://leetcode.com/problemset/all/") }
                "Practice" -> PracticeContent(isDarkTheme, isOledMode, accentColor, onOpenArena)
                "Contest" -> ContestScreen(isDarkTheme, isOledMode, accentColor, onOpenArena)
                "Analysis" -> AnalysisScreen(isDarkTheme, isOledMode, accentColor)
            }
        }
    }
}

// ============================================================================
// PARTNER SCREENS: FULLY RESTORED (Contest & Analysis)
// ============================================================================

@Composable
fun ContestScreen(isDarkTheme: Boolean, isOledMode: Boolean, accentColor: Color, onOpenArena: (String) -> Unit) {
    val colors = getAppColors(isDarkTheme, isOledMode)
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

    LazyColumn(modifier = Modifier.fillMaxSize().background(bg).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {

        // 1. Upcoming Contests
        item { Text("Upcoming Contests", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textC, modifier = Modifier.padding(bottom = 8.dp)) }
        items(upcoming) { c ->
            Card(
                modifier = Modifier.fillMaxWidth().clickable { onOpenArena(c.url) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = surface)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(colors["divider"]!!), contentAlignment = Alignment.Center) {
                        Text(c.badge, fontWeight = FontWeight.Bold, color = textC)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(c.name, fontWeight = FontWeight.Bold, color = textC)
                        Text(c.time, fontSize = 12.sp, color = secText)
                    }
                    Icon(Icons.Filled.Notifications, contentDescription = "Notify", tint = accentColor)
                }
            }
        }

        // 2. RESTORED: Daily Activity Grid
        item { Spacer(Modifier.height(16.dp)); Text("Daily Activity", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textC) }
        item {
            val levels = listOf(0,1,2,3,4,2,1, 0,0,1,3,4,4,2, 1,0,2,3,1,0,0, 1,2,4,3,2,1,0)
            Card(Modifier.fillMaxWidth(), RoundedCornerShape(12.dp), CardDefaults.cardColors(containerColor = surface)) {
                Column(Modifier.padding(16.dp)) {
                    Text("Last 4 Weeks", fontWeight = FontWeight.Bold, color = textC)
                    Spacer(Modifier.height(12.dp))
                    levels.chunked(7).forEach { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            row.forEach { lvl ->
                                val c = when (lvl) {
                                    0 -> colors["divider"]!!
                                    1 -> Color(0xFFD8F5D8)
                                    2 -> Color(0xFFA8E6A8)
                                    3 -> Color(0xFF6EDC6E)
                                    else -> Color(0xFF2E8B2E)
                                }
                                Box(Modifier.size(14.dp).background(c, RoundedCornerShape(3.dp)))
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                    }
                }
            }
        }

        // 3. RESTORED: Major Coding Contests (with pulsing animation)
        item { Spacer(Modifier.height(16.dp)); Text("Major Coding Contests", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textC) }
        items(contests.chunked(2)) { rowItems ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                rowItems.forEach { contest ->
                    val infinite = rememberInfiniteTransition(label = "pulse")
                    val scale by infinite.animateFloat(
                        initialValue = 0.98f, targetValue = 1.0f,
                        animationSpec = infiniteRepeatable(tween(1200), repeatMode = RepeatMode.Reverse), label = "scale"
                    )
                    Card(
                        modifier = Modifier.weight(1f).height(130.dp).graphicsLayer(scaleX = scale, scaleY = scale).clickable { onOpenArena(contest.url) },
                        colors = CardDefaults.cardColors(containerColor = if (isDarkTheme) contest.bgColor.copy(alpha = 0.15f) else contest.bgColor),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(if(isDarkTheme) contest.bgColor else Color.White), contentAlignment = Alignment.Center) {
                                    Text(contest.badge, fontWeight = FontWeight.Bold, color = if(isDarkTheme) textC else Color.Black)
                                }
                                Spacer(Modifier.width(8.dp))
                                Text(contest.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = textC, maxLines = 1)
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

@Composable
fun AnalysisScreen(isDarkTheme: Boolean, isOledMode: Boolean, accentColor: Color) {
    val colors = getAppColors(isDarkTheme, isOledMode)
    val bg = colors["bg"]!!
    val textC = colors["text"]!!
    val secText = colors["secondaryText"]!!

    val sessions = remember {
        listOf(
            AnalysisSession("LeetCode - Arrays", "45m", "12 Oct 2023", "LC", Color(0xFFC8F7C5)),
            AnalysisSession("GeeksforGeeks - Trees", "60m", "13 Oct 2023", "GFG", Color(0xFFFFC4C4)),
            AnalysisSession("Codeforces - DP", "50m", "14 Oct 2023", "CF", Color(0xFFD8C2FF)),
            AnalysisSession("LeetCode - Graphs", "40m", "15 Oct 2023", "LC", Color(0xFFB5E3FF)),
            AnalysisSession("Codeforces - Greedy", "55m", "16 Oct 2023", "CF", Color(0xFFF1F1F1)),
            AnalysisSession("GeeksforGeeks - Stack", "35m", "17 Oct 2023", "GFG", Color(0xFFDFFFE0))
        )
    }

    var selected by remember { mutableStateOf<AnalysisSession?>(null) }

    if (selected == null) {
        LazyColumn(modifier = Modifier.fillMaxSize().background(bg).padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item { Text("Analysis Sessions", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = textC, modifier = Modifier.padding(bottom = 8.dp)) }
            item {
                Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Session Platform", fontWeight = FontWeight.Bold, color = secText)
                    Text("Duration", fontWeight = FontWeight.Bold, color = secText)
                    Text("Date", fontWeight = FontWeight.Bold, color = secText)
                }
            }
            items(sessions) { s ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { selected = s },
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isDarkTheme) s.color.copy(alpha = 0.15f) else s.color)
                ) {
                    Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
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
        SessionDetailScreen(selected!!, isDarkTheme, isOledMode, accentColor) { selected = null }
    }
}

// RESTORED: Full Logic Details and PDFs
@Composable
fun SessionDetailScreen(session: AnalysisSession, isDarkTheme: Boolean, isOledMode: Boolean, accentColor: Color, onBack: () -> Unit) {
    val colors = getAppColors(isDarkTheme, isOledMode)
    val bg = colors["bg"]!!
    val surface = colors["surface"]!!
    val textC = colors["text"]!!
    val secText = colors["secondaryText"]!!

    LazyColumn(modifier = Modifier.fillMaxSize().background(bg).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = textC, modifier = Modifier.clickable { onBack() })
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
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = surface)) {
                Column(Modifier.padding(12.dp)) {
                    Text(title, fontWeight = FontWeight.Bold, color = textC)
                    Text(value, color = secText)
                }
            }
        }

        item { Spacer(Modifier.height(16.dp)); Text("PDFs", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = textC) }

        val pdfs = listOf("Your Code PDF", "Wrong vs Right PDF", "Correct Solution PDF")
        items(pdfs) { title ->
            Row(modifier = Modifier.fillMaxWidth().background(surface, RoundedCornerShape(10.dp)).padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Info, contentDescription = null, tint = accentColor)
                Spacer(Modifier.width(10.dp))
                Text(title, modifier = Modifier.weight(1f), color = textC)
                Button(onClick = { }, colors = ButtonDefaults.buttonColors(containerColor = accentColor, contentColor = Color.White)) {
                    Text("View")
                }
            }
        }
        item { Spacer(Modifier.height(20.dp)) }
    }
}


// ============================================================================
// CORE SCREENS (Auth, Home, Practice, Profile, Settings)
// ============================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(isDarkTheme: Boolean, isOledMode: Boolean, accentColor: Color, onLoginSuccess: () -> Unit) {
    val colors = getAppColors(isDarkTheme, isOledMode)
    val bg = colors["bg"]!!; val textC = colors["text"]!!; val secText = colors["secondaryText"]!!
    var isSignUp by remember { mutableStateOf(false) }; var email by remember { mutableStateOf("") }; var password by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().background(bg).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Box(modifier = Modifier.size(80.dp).clip(RoundedCornerShape(24.dp)).background(accentColor.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) { Icon(Icons.Default.Lock, contentDescription = "Logo", tint = accentColor, modifier = Modifier.size(40.dp)) }
        Spacer(modifier = Modifier.height(24.dp)); Text(if (isSignUp) "Create Account" else "Welcome Back", color = textC, fontSize = 28.sp, fontWeight = FontWeight.Bold); Spacer(modifier = Modifier.height(8.dp)); Text(if (isSignUp) "Sign up to start competing" else "Sign in to continue your journey", color = secText, fontSize = 15.sp); Spacer(modifier = Modifier.height(40.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = accentColor, focusedLabelColor = accentColor, unfocusedTextColor = textC, focusedTextColor = textC)); Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = accentColor, focusedLabelColor = accentColor, unfocusedTextColor = textC, focusedTextColor = textC)); Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onLoginSuccess, modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = accentColor, contentColor = Color.White)) { Text(if (isSignUp) "Sign Up" else "Sign In", fontSize = 16.sp, fontWeight = FontWeight.Bold) }; Spacer(modifier = Modifier.height(24.dp))
        OutlinedButton(onClick = onLoginSuccess, modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.outlinedButtonColors(contentColor = textC), border = androidx.compose.foundation.BorderStroke(1.dp, colors["divider"]!!)) { Text("Continue with Google", fontWeight = FontWeight.SemiBold) }; Spacer(modifier = Modifier.weight(1f))
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 24.dp)) { Text(if (isSignUp) "Already have an account? " else "Don't have an account? ", color = secText); Text(text = if (isSignUp) "Sign In" else "Sign Up", color = accentColor, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { isSignUp = !isSignUp }.padding(4.dp)) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(isDarkTheme: Boolean, isOledMode: Boolean, accentColor: Color, onBackPressed: () -> Unit, onToggleDarkTheme: () -> Unit, onToggleOledMode: () -> Unit, onSelectAccent: (Color) -> Unit, onSignOut: () -> Unit) {
    val colors = getAppColors(isDarkTheme, isOledMode)
    val bg = colors["bg"]!!; val surface = colors["surface"]!!; val textC = colors["text"]!!; val secText = colors["secondaryText"]!!
    val availableAccents = listOf(Color(0xFF2563EB), Color(0xFF10B981), Color(0xFF8B5CF6), Color(0xFFF97316), Color(0xFFE11D48))

    BackHandler { onBackPressed() }
    Scaffold(containerColor = bg, topBar = { CenterAlignedTopAppBar(colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = bg, titleContentColor = textC), title = { Text("Settings", fontWeight = FontWeight.Bold, fontSize = 20.sp) }, navigationIcon = { IconButton(onClick = onBackPressed) { Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = textC) } }) }) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 24.dp).verticalScroll(rememberScrollState())) {
            Spacer(modifier = Modifier.height(24.dp)); Text("Appearance", color = accentColor, fontSize = 14.sp, fontWeight = FontWeight.Bold); Spacer(modifier = Modifier.height(16.dp))
            Card(colors = CardDefaults.cardColors(containerColor = surface), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) { Column { Text("Dark Mode", color = textC, fontSize = 16.sp, fontWeight = FontWeight.SemiBold); Text("Switch themes", color = secText, fontSize = 13.sp) }; Switch(checked = isDarkTheme, onCheckedChange = { onToggleDarkTheme() }, colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = accentColor)) }
                    if (isDarkTheme) { Spacer(modifier = Modifier.height(16.dp)); HorizontalDivider(color = colors["divider"]!!); Spacer(modifier = Modifier.height(16.dp)); Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) { Column { Text("Deep Dark (OLED)", color = textC, fontSize = 16.sp, fontWeight = FontWeight.SemiBold); Text("Pure black background", color = secText, fontSize = 13.sp) }; Switch(checked = isOledMode, onCheckedChange = { onToggleOledMode() }, colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = accentColor)) } }
                }
            }
            Spacer(modifier = Modifier.height(32.dp)); Text("Theme Color", color = accentColor, fontSize = 14.sp, fontWeight = FontWeight.Bold); Spacer(modifier = Modifier.height(16.dp))
            Card(colors = CardDefaults.cardColors(containerColor = surface), shape = RoundedCornerShape(16.dp)) { Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceEvenly) { availableAccents.forEach { color -> Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(color).clickable { onSelectAccent(color) }.border(3.dp, if (accentColor == color) textC else Color.Transparent, CircleShape), contentAlignment = Alignment.Center) { if (accentColor == color) Icon(Icons.Default.Check, contentDescription = "Selected", tint = Color.White, modifier = Modifier.size(20.dp)) } } } }
            Spacer(modifier = Modifier.height(32.dp)); Text("Account", color = accentColor, fontSize = 14.sp, fontWeight = FontWeight.Bold); Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onSignOut, modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444).copy(alpha = 0.1f), contentColor = Color(0xFFEF4444)), elevation = ButtonDefaults.buttonElevation(0.dp)) { Icon(Icons.Default.ExitToApp, contentDescription = "Sign Out", modifier = Modifier.size(20.dp)); Spacer(modifier = Modifier.width(8.dp)); Text("Sign Out", fontSize = 16.sp, fontWeight = FontWeight.Bold) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(isDarkTheme: Boolean, isOledMode: Boolean, accentColor: Color, onBackPressed: () -> Unit, onOpenSettings: () -> Unit) {
    val colors = getAppColors(isDarkTheme, isOledMode); val bg = colors["bg"]!!; val surface = colors["surface"]!!; val textC = colors["text"]!!; val secText = colors["secondaryText"]!!

    BackHandler { onBackPressed() }
    Scaffold(
        containerColor = bg,
        topBar = { CenterAlignedTopAppBar(colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = bg, titleContentColor = textC), title = { Text("Profile", fontWeight = FontWeight.Bold, fontSize = 20.sp) }, navigationIcon = { IconButton(onClick = onBackPressed) { Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = textC) } }, actions = { IconButton(onClick = onOpenSettings) { Icon(Icons.Default.Settings, contentDescription = "Settings", tint = textC) } }) }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).verticalScroll(rememberScrollState()).padding(horizontal = 24.dp).navigationBarsPadding()) {
            Spacer(modifier = Modifier.height(20.dp))
            Box(modifier = Modifier.align(Alignment.CenterHorizontally)) { Box(modifier = Modifier.size(110.dp).clip(CircleShape).background(surface).border(2.dp, accentColor, CircleShape), contentAlignment = Alignment.Center) { Icon(Icons.Default.Person, contentDescription = "Avatar", modifier = Modifier.size(60.dp), tint = secText) }; Box(modifier = Modifier.align(Alignment.BottomEnd).size(32.dp).clip(CircleShape).background(accentColor).clickable { }, contentAlignment = Alignment.Center) { Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(16.dp), tint = Color.White) } }
            Spacer(modifier = Modifier.height(16.dp)); Text("Ujjwal", color = textC, fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterHorizontally)); Text("@ujjwal_01", color = secText, fontSize = 16.sp, modifier = Modifier.align(Alignment.CenterHorizontally)); Spacer(modifier = Modifier.height(32.dp))
            Text("About Me", color = textC, fontSize = 18.sp, fontWeight = FontWeight.Bold); Spacer(modifier = Modifier.height(12.dp))
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = surface), shape = RoundedCornerShape(16.dp)) { Column(modifier = Modifier.padding(16.dp)) { Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("Bio", color = secText, fontSize = 14.sp); Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(16.dp), tint = accentColor) }; Spacer(modifier = Modifier.height(8.dp)); Text("AI Engineering student. Building cool things at hackathons. Passionate about algorithms and web dev! 🚀", color = textC, fontSize = 15.sp, lineHeight = 22.sp) } }
            Spacer(modifier = Modifier.height(32.dp)); Text("Recent Activity", color = textC, fontSize = 18.sp, fontWeight = FontWeight.Bold); Spacer(modifier = Modifier.height(12.dp))
            listOf(Triple("LeetCode", "Two Sum", "Solved 2 hours ago"), Triple("Codeforces", "Watermelon", "Solved yesterday"), Triple("HackerRank", "Array Manipulation", "Attempted 2 days ago"), Triple("AtCoder", "Frog 1", "Solved 3 days ago"), Triple("CodeChef", "Starters 112", "Rank 420")).forEach { (platform, problem, time) ->
                Card(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp), colors = CardDefaults.cardColors(containerColor = surface), shape = RoundedCornerShape(12.dp)) { Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) { Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(accentColor)); Spacer(modifier = Modifier.width(16.dp)); Column { Text(problem, color = textC, fontSize = 16.sp, fontWeight = FontWeight.SemiBold); Text("$platform • $time", color = secText, fontSize = 13.sp) } } }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun PracticeContent(isDarkTheme: Boolean, isOledMode: Boolean, accentColor: Color, onOpenArena: (String) -> Unit) {
    val colors = getAppColors(isDarkTheme, isOledMode); val surface = colors["surface"]!!; val textC = colors["text"]!!; val secText = colors["secondaryText"]!!
    val platforms = listOf(Platform("LeetCode", "https://leetcode.com/problemset/all/", Color(0xFFFEF3C7), Color(0xFFD97706)), Platform("HackerRank", "https://www.hackerrank.com/domains", Color(0xFFD1FAE5), Color(0xFF059669)), Platform("Codeforces", "https://codeforces.com/problemset", Color(0xFFDBEAFE), Color(0xFF2563EB)), Platform("CodeChef", "https://www.codechef.com/practice", Color(0xFFFFEDD5), Color(0xFFEA580C)))
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 20.dp)) {
        Text("Practice Arena", color = textC, fontSize = 28.sp, fontWeight = FontWeight.Bold); Spacer(modifier = Modifier.height(8.dp)); Text("Configure a sample test or choose a platform.", color = secText, fontSize = 15.sp); Spacer(modifier = Modifier.height(24.dp))
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = surface), shape = RoundedCornerShape(16.dp)) { Column(Modifier.padding(16.dp)) { Text("Generate Sample Test", fontWeight = FontWeight.Bold, color = textC, fontSize = 16.sp); Spacer(Modifier.height(12.dp)); Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) { OutlinedButton(onClick = {}, modifier = Modifier.weight(1f), contentPadding = PaddingValues(0.dp)) { Text("Difficulty ▾", color = textC, fontSize = 12.sp) }; OutlinedButton(onClick = {}, modifier = Modifier.weight(1f), contentPadding = PaddingValues(0.dp)) { Text("Duration ▾", color = textC, fontSize = 12.sp) }; OutlinedButton(onClick = {}, modifier = Modifier.weight(1f), contentPadding = PaddingValues(0.dp)) { Text("Questions ▾", color = textC, fontSize = 12.sp) } }; Spacer(Modifier.height(12.dp)); Button(onClick = { }, modifier = Modifier.fillMaxWidth().height(48.dp), colors = ButtonDefaults.buttonColors(containerColor = accentColor)) { Text("START TEST", color = Color.White, fontWeight = FontWeight.Bold) } } }
        Spacer(modifier = Modifier.height(24.dp)); Text("Or jump into a platform:", color = textC, fontSize = 16.sp, fontWeight = FontWeight.Bold); Spacer(modifier = Modifier.height(16.dp))
        LazyVerticalGrid(columns = GridCells.Fixed(2), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp), contentPadding = PaddingValues(bottom = 20.dp)) { items(platforms) { platform -> Card(modifier = Modifier.fillMaxWidth().height(140.dp).clickable { onOpenArena(platform.url) }, colors = CardDefaults.cardColors(containerColor = surface), shape = RoundedCornerShape(20.dp)) { Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) { Box(modifier = Modifier.size(56.dp).background(if (isDarkTheme) platform.darkColor.copy(alpha = 0.2f) else platform.softColor, CircleShape), contentAlignment = Alignment.Center) { Text(platform.name.first().toString(), color = if (isDarkTheme) platform.softColor else platform.darkColor, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold) }; Spacer(modifier = Modifier.height(16.dp)); Text(platform.name, color = textC, fontSize = 15.sp, fontWeight = FontWeight.SemiBold) } } } }
    }
}

@Composable
fun ArenaScreen(url: String, accentColor: Color, onBackPressed: () -> Unit) {
    BackHandler { onBackPressed() }
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { context -> WebView(context).apply { settings.javaScriptEnabled = true; settings.domStorageEnabled = true; webViewClient = WebViewClient(); loadUrl(url) } }, modifier = Modifier.fillMaxSize())
        FloatingActionButton(onClick = { }, containerColor = accentColor, contentColor = Color.White, shape = CircleShape, modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp).size(72.dp).shadow(12.dp, CircleShape)) { Icon(Icons.Default.PlayArrow, contentDescription = "Voice Bot", modifier = Modifier.size(36.dp)) }
    }
}

@Composable
fun HomeContent(isDarkTheme: Boolean, isOledMode: Boolean, accentColor: Color, onThemeToggle: () -> Unit, onOpenProfile: () -> Unit, onEnterArenaClick: () -> Unit) {
    val colors = getAppColors(isDarkTheme, isOledMode); val surface = colors["surface"]!!; val textC = colors["text"]!!; val secText = colors["secondaryText"]!!; val divColor = colors["divider"]!!
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 20.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(top = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) { Text("Hello, Ujjwal! 👋", color = textC, fontSize = 28.sp, fontWeight = FontWeight.Bold); Spacer(modifier = Modifier.height(4.dp)); Text("Current Rating: 1542 | LeetCode", color = secText, fontSize = 15.sp, fontWeight = FontWeight.Medium) }
            Card(modifier = Modifier.clickable { onThemeToggle() }, colors = CardDefaults.cardColors(containerColor = surface), shape = RoundedCornerShape(12.dp)) { Text(if (isDarkTheme) "☀️" else "🌙", color = accentColor, fontSize = 18.sp, modifier = Modifier.padding(10.dp)) }
            Spacer(modifier = Modifier.width(12.dp))
            Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(surface).clickable { onOpenProfile() }.border(2.dp, accentColor, CircleShape), contentAlignment = Alignment.Center) { Icon(Icons.Default.Person, contentDescription = "Profile", tint = secText) }
        }
        Spacer(modifier = Modifier.height(32.dp)); Text("Upcoming Contests", color = textC, fontSize = 18.sp, fontWeight = FontWeight.Bold); Spacer(modifier = Modifier.height(16.dp))
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = surface), shape = RoundedCornerShape(20.dp)) { Column(modifier = Modifier.padding(20.dp)) { Text("LeetCode Weekly 385", color = textC, fontSize = 20.sp, fontWeight = FontWeight.Bold); Spacer(modifier = Modifier.height(8.dp)); Text("Difficulty: Medium - Hard", color = secText, fontSize = 14.sp); Spacer(modifier = Modifier.height(12.dp)); Row(verticalAlignment = Alignment.CenterVertically) { Box(modifier = Modifier.size(8.dp).background(accentColor, CircleShape)); Spacer(modifier = Modifier.width(8.dp)); Text("Starts in: 02:14:00", color = accentColor, fontSize = 15.sp, fontWeight = FontWeight.Bold) }; Spacer(modifier = Modifier.height(20.dp)); Button(onClick = { onEnterArenaClick() }, modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = accentColor, contentColor = Color.White)) { Text("Enter Arena", fontSize = 16.sp, fontWeight = FontWeight.Bold) } } }
        Spacer(modifier = Modifier.height(32.dp)); Text("Your Progress", color = textC, fontSize = 18.sp, fontWeight = FontWeight.Bold); Spacer(modifier = Modifier.height(16.dp))
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = surface), shape = RoundedCornerShape(20.dp)) { Column(modifier = Modifier.padding(20.dp)) { Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("Daily Goal", color = textC, fontSize = 16.sp, fontWeight = FontWeight.Bold); Text("3/5 Problems", color = accentColor, fontSize = 16.sp, fontWeight = FontWeight.Bold) }; Spacer(modifier = Modifier.height(16.dp)); LinearProgressIndicator(progress = { 0.6f }, modifier = Modifier.fillMaxWidth().height(8.dp), color = accentColor, trackColor = divColor, strokeCap = StrokeCap.Round); Spacer(modifier = Modifier.height(24.dp)); Box(modifier = Modifier.fillMaxWidth().height(110.dp).drawBehind { drawRoundRect(color = divColor, style = Stroke(width = 2.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 10f), 0f)), cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx())) }, contentAlignment = Alignment.Center) { Text("Rating Trend Chart Placeholder", color = secText, fontSize = 14.sp, fontWeight = FontWeight.Medium) } } }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

// ============================================================================
// DATA CLASSES
// ============================================================================
data class Platform(val name: String, val url: String, val softColor: Color, val darkColor: Color)
data class AnalysisSession(val title: String, val duration: String, val date: String, val platform: String, val color: Color)
data class ContestItem(val title: String, val badge: String, val time: String, val date: String, val tier: String, val bgColor: Color, val url: String)
data class UpcomingContest(val name: String, val badge: String, val time: String, val duration: String, val url: String)