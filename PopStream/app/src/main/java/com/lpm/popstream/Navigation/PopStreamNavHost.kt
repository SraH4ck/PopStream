package com.lpm.popstream.Navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lpm.popstream.Screens.ListDetailsScreen
import com.lpm.popstream.Model.Movie
import com.lpm.popstream.Screens.HomeScreen
import com.lpm.popstream.Screens.LikesScreen
import com.lpm.popstream.Screens.LoginScreen
import com.lpm.popstream.Screens.MyListsScreen
import com.lpm.popstream.Screens.PendingScreen
import com.lpm.popstream.Screens.RegisterScreen
import com.lpm.popstream.Screens.SplashScreen
import com.lpm.popstream.Screens.WatchedScreen
import com.lpm.popstream.Screens.WatchingScreen
import com.lpm.popstream.ViewModel.ThemeViewModel

@Composable
fun PopStreamNavHost(themeViewModel: ThemeViewModel) {
    val navController = rememberNavController()

    // Listas compartidas para las películas
    val favoriteMovies = remember { mutableStateListOf<Movie>() }
    val watchingMovies = remember { mutableStateListOf<Movie>() }
    val watchedMovies = remember { mutableStateListOf<Movie>() }
    val pendingMovies = remember { mutableStateListOf<Movie>() }
    val customLists = remember { mutableStateMapOf<String, MutableList<Movie>>() } // Listas personalizadas

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("home") {
            HomeScreen(
                navController = navController,
                favoriteMovies = favoriteMovies,
                watchingMovies = watchingMovies,
                watchedMovies = watchedMovies,
                pendingMovies = pendingMovies,
                customLists = customLists, // Pasar listas personalizadas
                themeViewModel = themeViewModel
            )
        }
        composable("favorites") {
            LikesScreen(
                navController = navController,
                favoriteMovies = favoriteMovies
            )
        }
        composable("pending") {
            PendingScreen(
                navController = navController,
                pendingMovies = pendingMovies
            )
        }
        composable("watched") {
            WatchedScreen(
                navController = navController,
                watchedMovies = watchedMovies
            )
        }
        composable("watching") {
            WatchingScreen(
                navController = navController,
                watchingMovies = watchingMovies
            )
        }
        composable("myLists") {
            MyListsScreen(navController, customLists)
        }
        composable(
            route = "listDetails/{listName}",
            arguments = listOf(navArgument("listName") { type = NavType.StringType })
        ) { backStackEntry ->
            val listName = backStackEntry.arguments?.getString("listName") ?: return@composable
            val movies = customLists[listName]?.toMutableList() ?: mutableListOf()
            ListDetailsScreen(navController, listName, movies)
        }
    }
}
