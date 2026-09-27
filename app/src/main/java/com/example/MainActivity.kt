package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.CurrencyExchange
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.database.AppDatabase
import com.example.data.repository.MoneyRepository
import com.example.ui.components.FloatingLiquidGlassNavBar
import com.example.ui.components.LiquidGlassBackground
import com.example.ui.screens.AllocateScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ForexScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MoneyViewModel
import com.example.ui.viewmodel.MoneyViewModelFactory
import com.example.util.GoalNotificationHelper

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}

enum class AppTab(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    DASHBOARD("Dashboard", Icons.Filled.Dashboard, Icons.Outlined.Dashboard),
    ALLOCATE("Allocate", Icons.Filled.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet),
    HISTORY("History", Icons.Filled.History, Icons.Outlined.History),
    SETTINGS("Settings", Icons.Filled.Settings, Icons.Outlined.Settings)
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GoalNotificationHelper.createNotificationChannel(applicationContext)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current.applicationContext
            val database = remember { AppDatabase.getDatabase(context) }
            val repository = remember { MoneyRepository(database) }
            val viewModel: MoneyViewModel = viewModel(factory = MoneyViewModelFactory(repository))
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()

            MyApplicationTheme(themeMode = themeMode) {
                MoneyApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MoneyApp(viewModel: MoneyViewModel) {
    val context = LocalContext.current
    var currentTab by remember { mutableStateOf(AppTab.DASHBOARD) }
    var settingsSubPage by remember { mutableStateOf(com.example.ui.screens.SettingsSubPage.ROOT) }

    // Intercept back button hierarchically:
    // 1. If inside a Settings sub-page, return to root Settings
    // 2. Otherwise if on another tab, return to Dashboard tab
    BackHandler(enabled = currentTab == AppTab.SETTINGS && settingsSubPage != com.example.ui.screens.SettingsSubPage.ROOT) {
        settingsSubPage = com.example.ui.screens.SettingsSubPage.ROOT
    }

    BackHandler(enabled = currentTab != AppTab.DASHBOARD && (currentTab != AppTab.SETTINGS || settingsSubPage == com.example.ui.screens.SettingsSubPage.ROOT)) {
        currentTab = AppTab.DASHBOARD
    }

    // Request POST_NOTIFICATIONS permission on Android 13+ (API 33+)
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!hasPermission) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    // Observe goals and currency to trigger milestone push notifications (50%, 75%, 100%)
    val goalsWithProgress by viewModel.goalsWithProgress.collectAsStateWithLifecycle()
    val currencySymbol by viewModel.currencySymbol.collectAsStateWithLifecycle()

    LaunchedEffect(goalsWithProgress, currencySymbol) {
        if (goalsWithProgress.isNotEmpty()) {
            viewModel.checkAndTriggerGoalMilestones(context, goalsWithProgress, currencySymbol)
        }
    }

    LiquidGlassBackground {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets.safeDrawing,
            bottomBar = {
                FloatingLiquidGlassNavBar(
                    items = AppTab.entries.toList(),
                    selectedItem = currentTab,
                    onItemSelected = { selectedTab ->
                        currentTab = selectedTab
                        if (selectedTab != AppTab.SETTINGS) {
                            settingsSubPage = com.example.ui.screens.SettingsSubPage.ROOT
                        }
                    },
                    getItemLabel = { it.label },
                    getItemIcons = { Pair(it.selectedIcon, it.unselectedIcon) }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentTab) {
                    AppTab.DASHBOARD -> DashboardScreen(
                        viewModel = viewModel,
                        onAddIncomeClick = { currentTab = AppTab.ALLOCATE },
                        onViewAllActivityClick = { currentTab = AppTab.HISTORY }
                    )
                    AppTab.ALLOCATE -> AllocateScreen(
                        viewModel = viewModel,
                        onOpenForexClick = {
                            currentTab = AppTab.SETTINGS
                            settingsSubPage = com.example.ui.screens.SettingsSubPage.FOREX
                        }
                    )
                    AppTab.HISTORY -> HistoryScreen(viewModel = viewModel)
                    AppTab.SETTINGS -> SettingsScreen(
                        viewModel = viewModel,
                        onNavigateToAllocate = {
                            currentTab = AppTab.ALLOCATE
                            settingsSubPage = com.example.ui.screens.SettingsSubPage.ROOT
                        },
                        currentSubPage = settingsSubPage,
                        onSubPageChange = { settingsSubPage = it }
                    )
                }
            }
        }
    }
}
