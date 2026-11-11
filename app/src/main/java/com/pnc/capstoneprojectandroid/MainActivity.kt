package com.pnc.capstoneprojectandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pnc.capstoneprojectandroid.core.CustomerHomeScreen
import com.pnc.capstoneprojectandroid.ui.orderhistory.OrderHistoryApp
import com.pnc.capstoneprojectandroid.features.products.presentation.composables.ProductDetailsScreen
import com.pnc.capstoneprojectandroid.features.products.presentation.composables.ProductsScreen
import com.pnc.capstoneprojectandroid.ui.theme.CapstoneProjectAndroidTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CapstoneProjectAndroidTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()

                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = "customer_home"
                        ) {
                            composable(route = "customer_home") {
                                // Apply padding here for screens without their own TopAppBar
                                Box(modifier = Modifier.padding(innerPadding)) {
                                    CustomerHomeScreen(
                                        onNavigateToProducts = { navController.navigate("products") },
                                        onNavigateToOrders = { navController.navigate("order_history") },
                                        onProductClick = { productId ->
                                            navController.navigate("products/$productId")
                                        }
                                    )
                                }
                            }
                            composable("products") {
                                ProductsScreen(
                                    onProductClick = { productId ->
                                        navController.navigate("products/$productId")
                                    },
                                    onBack = { navController.popBackStack() }
                                )
                            }
                            composable(
                                route = "products/{productId}",
                                arguments = listOf(navArgument("productId") { type = NavType.IntType })
                            ) {
                                ProductDetailsScreen(onBack = { navController.popBackStack() })
                            }
                            composable(route = "order_history") {
                                OrderHistoryApp(
//                                  onBack = { navController.popBackStack() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}