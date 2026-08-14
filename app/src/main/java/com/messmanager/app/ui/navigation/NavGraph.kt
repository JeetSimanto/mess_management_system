package com.messmanager.app.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.messmanager.app.ui.borrow.BorrowScreen
import com.messmanager.app.ui.components.BottomNavBar
import com.messmanager.app.ui.components.TopBar
import com.messmanager.app.ui.contribution.ContributionScreen
import com.messmanager.app.ui.dashboard.DashboardScreen
import com.messmanager.app.ui.grocery.GroceryScreen
import com.messmanager.app.ui.meal.MealTrackerScreen
import com.messmanager.app.ui.settings.SettingsScreen
import com.messmanager.app.ui.utility.UtilityScreen
import com.messmanager.app.ui.theme.DarkPrimary
import com.messmanager.app.ui.theme.DarkPrimaryGlow
import com.messmanager.app.ui.welcome.AuthViewModel
import com.messmanager.app.ui.welcome.CreateMessScreen
import com.messmanager.app.ui.welcome.JoinMessScreen
import com.messmanager.app.ui.welcome.WelcomeScreen
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = hiltViewModel(),
    onGoogleSignInClick: () -> Unit
) {
    val authState by authViewModel.uiState.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Welcome.route

    if (authState.isAuthInitializing) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(DarkPrimaryGlow),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "App Logo",
                        tint = DarkPrimary,
                        modifier = Modifier.size(44.dp)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                CircularProgressIndicator(
                    color = DarkPrimary,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        return
    }

    LaunchedEffect(authState.isMessCreatedOrJoined, authState.isAuthInitializing) {
        if (!authState.isAuthInitializing && authState.isMessCreatedOrJoined && currentRoute == Screen.Welcome.route) {
            navController.navigate(Screen.Dashboard.route) {
                popUpTo(Screen.Welcome.route) { inclusive = true }
            }
        }
    }

    val isMainTab = currentRoute in listOf(
        Screen.Dashboard.route,
        Screen.Grocery.route,
        Screen.Utility.route,
        Screen.Meals.route,
        Screen.Contributions.route
    )

    val activeMess = authState.activeMess
    val monthText = if (activeMess != null) {
        "${Month.of(activeMess.month).getDisplayName(TextStyle.SHORT, Locale.ENGLISH)} ${activeMess.year}"
    } else ""

    Scaffold(
        topBar = {
            if (isMainTab && activeMess != null) {
                TopBar(
                    messName = activeMess.name,
                    monthYearText = monthText,
                    onOpenSettings = { navController.navigate(Screen.Settings.route) },
                    onOpenBorrows = { navController.navigate(Screen.Borrows.route) }
                )
            }
        },
        bottomBar = {
            if (isMainTab && activeMess != null) {
                BottomNavBar(
                    currentRoute = currentRoute,
                    onNavigate = { screen ->
                        navController.navigate(screen.route) {
                            popUpTo(Screen.Dashboard.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        val startDestination = if (authState.isMessCreatedOrJoined) {
            Screen.Dashboard.route
        } else {
            Screen.Welcome.route
        }

        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Welcome.route) {
                WelcomeScreen(
                    uiState = authState,
                    onGoogleSignInClick = onGoogleSignInClick,
                    onCreateMessClick = { navController.navigate(Screen.CreateMess.route) },
                    onJoinMessClick = { navController.navigate(Screen.JoinMess.route) }
                )
            }

            composable(Screen.CreateMess.route) {
                CreateMessScreen(
                    uiState = authState,
                    onCreateMess = { name, month, year ->
                        authViewModel.createMess(name, month, year)
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Screen.JoinMess.route) {
                JoinMessScreen(
                    uiState = authState,
                    onJoinMess = { inviteCode ->
                        authViewModel.joinMess(inviteCode)
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Screen.Dashboard.route) {
                DashboardScreen()
            }

            composable(Screen.Grocery.route) {
                GroceryScreen()
            }

            composable(Screen.Utility.route) {
                UtilityScreen()
            }

            composable(Screen.Meals.route) {
                MealTrackerScreen()
            }

            composable(Screen.Contributions.route) {
                ContributionScreen()
            }

            composable(Screen.Settings.route) {
                SettingsScreen(onNavigateToWelcome = {
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(0) { inclusive = true }
                    }
                })
            }

            composable(Screen.Borrows.route) {
                BorrowScreen()
            }
        }
    }
}
