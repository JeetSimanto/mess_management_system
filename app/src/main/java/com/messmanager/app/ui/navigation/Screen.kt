package com.messmanager.app.ui.navigation

sealed class Screen(val route: String, val title: String = "") {
    object Welcome : Screen("welcome", "Welcome")
    object CreateMess : Screen("create_mess", "Create Mess")
    object JoinMess : Screen("join_mess", "Join Mess")

    // Main Bottom Nav Tabs
    object Dashboard : Screen("dashboard", "Home")
    object Grocery : Screen("grocery", "Grocery")
    object Utility : Screen("utility", "Utility")
    object Meals : Screen("meals", "Meals")
    object Contributions : Screen("contributions", "Deposits")

    // Settings
    object Settings : Screen("settings", "Settings")
    object Borrows : Screen("borrows", "Borrows")
}
