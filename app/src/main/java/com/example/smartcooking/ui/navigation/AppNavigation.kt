package com.example.smartcooking.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.smartcooking.ui.profile.ProfileScreen
import com.example.smartcooking.ui.recipe.CalorieCalculatorScreen
import com.example.smartcooking.ui.recipe.RecipeDetailScreen
import com.example.smartcooking.ui.recipe.RecipeListScreen

object Routes {
    const val PROFILE = "profile"
    const val RECIPE_LIST = "recipe_list"
    const val CALCULATOR = "calculator"
    const val RECIPE_DETAIL = "recipe_detail/{recipeId}"
    fun recipeDetail(recipeId: String) = "recipe_detail/$recipeId"
}

private data class BottomNavItem(val route: String, val label: String, val icon: ImageVector)

private val bottomItems = listOf(
    BottomNavItem(Routes.PROFILE, "个人档案", Icons.Filled.Person),
    BottomNavItem(Routes.RECIPE_LIST, "菜谱", Icons.Filled.RestaurantMenu),
    BottomNavItem(Routes.CALCULATOR, "计算器", Icons.Filled.Calculate)
)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute in listOf(Routes.PROFILE, Routes.RECIPE_LIST, Routes.CALCULATOR)) {
                NavigationBar {
                    bottomItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.PROFILE,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.PROFILE) { ProfileScreen() }
            composable(Routes.RECIPE_LIST) {
                RecipeListScreen(onRecipeClick = { recipeId ->
                    navController.navigate(Routes.recipeDetail(recipeId))
                })
            }
            composable(Routes.CALCULATOR) { CalorieCalculatorScreen() }
            composable(
                route = Routes.RECIPE_DETAIL,
                arguments = listOf(navArgument("recipeId") { type = NavType.StringType })
            ) { backStackEntry ->
                val recipeId = backStackEntry.arguments?.getString("recipeId").orEmpty()
                RecipeDetailScreen(recipeId = recipeId, onBack = { navController.popBackStack() })
            }
        }
    }
}
