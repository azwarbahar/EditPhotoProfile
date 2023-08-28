package com.azwar.editphotoprofile

//import androidx.compose.runtime.Composable
//import androidx.navigation.NavHostController
//import androidx.navigation.NavType
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.navArgument

//@Composable
//fun NavGraph(navController: NavHostController) {
//    NavHost(
//        navController = navController,
//        startDestination = "home"
//    ) {
//        composable(route = "home") {
//            //call LoginScreen composable function here
//        }
//
//        composable(route = "detail/{image}",
//            arguments = listOf(navArgument("image") {
//                type = NavType.StringType
//            })
//        ) {
//            val image = requireNotNull(it.arguments).getString("image")
//            Detail(image)
//        }
//    }
//}