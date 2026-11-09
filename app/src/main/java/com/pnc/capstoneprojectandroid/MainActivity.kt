package com.pnc.capstoneprojectandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "products",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("products") {
                            ProductsScreen(onProductClick = { id -> navController.navigate("products/$id") })
                        }
                        composable(
                            route = "products/{productId}",
                            arguments = listOf(navArgument("productId") { type = NavType.IntType })
                        ) {
                            ProductDetailsScreen(onBack = { navController.popBackStack() })
                        }
                    }
                }
            }
        }
    }
}



