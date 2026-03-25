package com.example.myapplication

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.screens.AnalysisScreen
import com.example.myapplication.ui.screens.ContestScreen
import com.example.myapplication.ui.screens.HomeScreen
import com.example.myapplication.ui.screens.PracticeScreen
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                // --- GLOBAL APP STATE ---
                var isLoggedIn by remember { mutableStateOf(false) }
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
                        onSignOut = { isLoggedIn = false; isSettingsOpen = false; isProfileOpen = false }
                    )
                } else if (isProfileOpen) {
                    ProfileScreen(isDarkTheme, isOledMode, accentColor, onBackPressed = { isProfileOpen = false }) { isSettingsOpen = true }
                } else if (isArenaOpen) {
                    ArenaScreen(arenaUrl, accentColor) { isArenaOpen = false }
                } else {
                    MainScaffold(
                        isDarkTheme, isOledMode, accentColor,
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
fun MainScaffold(
    isDarkTheme: Boolean,
    isOledMode: Boolean,
    accentColor: Color,
    onThemeToggle: () -> Unit,
    onOpenArena: (String) -> Unit,
    onOpenProfile: () -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val colors = getAppColors(isDarkTheme, isOledMode)
    val bg = colors["bg"]!!
    val surface = colors["surface"]!!
    val secText = colors["secondaryText"]!!

    val navItems = listOf(
        Triple("Home", Icons.Default.Home, "home"),
        Triple("Contest", Icons.Default.Star, "contest"),
        Triple("Practice", Icons.Default.PlayArrow, "practice"),
        Triple("Analysis", Icons.Default.Settings, "analysis")
    )

    Scaffold(
        containerColor = bg,
        bottomBar = {
            NavigationBar(containerColor = surface, tonalElevation = 8.dp) {
                navItems.forEach { (title, icon, route) ->
                    NavigationBarItem(
                        selected = currentRoute == route,
                        onClick = {
                            navController.navigate(route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(icon, contentDescription = title) },
                        label = { Text(title, fontWeight = FontWeight.Medium) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = accentColor,
                            selectedTextColor = accentColor,
                            indicatorColor = accentColor.copy(alpha = 0.15f),
                            unselectedIconColor = secText,
                            unselectedTextColor = secText
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen(isDarkTheme, isOledMode, accentColor, onThemeToggle, onOpenProfile) {
                    onOpenArena("https://leetcode.com/problemset/all/")
                }
            }
            composable("contest") {
                ContestScreen(isDarkTheme, isOledMode, accentColor, onOpenArena)
            }
            composable("practice") {
                PracticeScreen(isDarkTheme, isOledMode, accentColor, onOpenArena)
            }
            composable("analysis") {
                AnalysisScreen(isDarkTheme, isOledMode, accentColor)
            }
        }
    }
}


// ============================================================================
// CORE SCREENS (Auth, Profile, Settings)
// ============================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(isDarkTheme: Boolean, isOledMode: Boolean, accentColor: Color, onLoginSuccess: () -> Unit) {
    val colors = getAppColors(isDarkTheme, isOledMode)
    val bg = colors["bg"]!!; val textC = colors["text"]!!; val secText = colors["secondaryText"]!!
    var isSignUp by remember { mutableStateOf(false) }; var email by remember { mutableStateOf("") }; var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    var isGoogleLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val googleSignInHelper = remember(context) { GoogleSignInHelper(context) }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        googleSignInHelper.handleSignInResult(
            data = result.data,
            onSuccess = { isGoogleLoading = false; onLoginSuccess() },
            onFailure = { error -> isGoogleLoading = false; errorMessage = error }
        )
    }

    Column(modifier = Modifier.fillMaxSize().background(bg).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Box(modifier = Modifier.size(80.dp).clip(RoundedCornerShape(24.dp)).background(accentColor.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) { Icon(Icons.Default.Lock, contentDescription = "Logo", tint = accentColor, modifier = Modifier.size(40.dp)) }
        Spacer(modifier = Modifier.height(24.dp)); Text(if (isSignUp) "Create Account" else "Welcome Back", color = textC, fontSize = 28.sp, fontWeight = FontWeight.Bold); Spacer(modifier = Modifier.height(8.dp)); Text(if (isSignUp) "Sign up to start competing" else "Sign in to continue your journey", color = secText, fontSize = 15.sp); Spacer(modifier = Modifier.height(40.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = accentColor, focusedLabelColor = accentColor, unfocusedTextColor = textC, focusedTextColor = textC)); Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = accentColor, focusedLabelColor = accentColor, unfocusedTextColor = textC, focusedTextColor = textC)); Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onLoginSuccess, modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = accentColor, contentColor = Color.White)) { Text(if (isSignUp) "Sign Up" else "Sign In", fontSize = 16.sp, fontWeight = FontWeight.Bold) }; Spacer(modifier = Modifier.height(24.dp))
        if (errorMessage != null) { Text(errorMessage!!, color = Color(0xFFEF4444), fontSize = 13.sp, modifier = Modifier.padding(bottom = 8.dp)) }
        OutlinedButton(
            onClick = {
                errorMessage = null
                isGoogleLoading = true
                googleSignInLauncher.launch(googleSignInHelper.getSignInIntent())
            },
            enabled = !isGoogleLoading,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = textC),
            border = androidx.compose.foundation.BorderStroke(1.dp, colors["divider"]!!)
        ) {
            if (isGoogleLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = accentColor, strokeWidth = 2.dp)
            } else {
                Text("Continue with Google", fontWeight = FontWeight.SemiBold)
            }
        }
        Spacer(modifier = Modifier.weight(1f))
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
fun ArenaScreen(url: String, accentColor: Color, onBackPressed: () -> Unit) {
    BackHandler { onBackPressed() }
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { context -> WebView(context).apply { settings.javaScriptEnabled = true; settings.domStorageEnabled = true; webViewClient = WebViewClient(); loadUrl(url) } }, modifier = Modifier.fillMaxSize())
        FloatingActionButton(onClick = { }, containerColor = accentColor, contentColor = Color.White, shape = CircleShape, modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp).size(72.dp).shadow(12.dp, CircleShape)) { Icon(Icons.Default.PlayArrow, contentDescription = "Voice Bot", modifier = Modifier.size(36.dp)) }
    }
}