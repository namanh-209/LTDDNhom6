package com.example.bookstore

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bookstore.Screen.LoginScreen
import com.example.bookstore.Screen.RegisterScreen

@Composable
fun Appnavgraph(){
    val navController = rememberNavController()

    NavHost(
        navController=navController,
        startDestination ="login"
    ){
        composable("login"){
            LoginScreen(
                onRegisterClick = {
                    navController.navigate("register")
                }
            )
        }
        composable("register"){
            RegisterScreen(
                onLoginClick = {
                    navController.navigate("login"){
                        popUpTo("register"){
                            inclusive=true
                        }
                    }
                }
            )
        }
    }
}