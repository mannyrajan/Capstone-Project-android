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
import com.pnc.capstoneprojectandroid.features.orders.presentation.composables.OrderHistoryApp
import com.pnc.capstoneprojectandroid.features.products.presentation.composables.ProductDetailsScreen
import com.pnc.capstoneprojectandroid.features.products.presentation.composables.ProductsScreen
import com.pnc.capstoneprojectandroid.features.products.presentation.composables.SavedProductsScreen
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
                                        // removed onNavigateToRecentlyViewed callback since it's moved to carousel
                                        onProductClick = { productId ->
                                            navController.navigate("products/$productId")
                                        }
                                    )
                                }
                            }
                            // removed recently viewed screen composable since it's moved to carousel
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
                                // adding back in padding to the details screen
                                Box(modifier = Modifier.padding(innerPadding)) {
                                    ProductDetailsScreen(onBack = { navController.popBackStack() })
                                }
                            }
                            composable(route = "order_history") {
                                OrderHistoryApp(
                                  onBack = { navController.popBackStack() }
                                )
                            }
                            composable(route = "saved") {
                                SavedProductsScreen(
                                    onProductClick = { id -> navController.navigate("products/$id") },
                                    onBack = { navController.popBackStack() }
                                )
                            }
                        }

                    }
                }
            }
        }
    }
}