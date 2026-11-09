package com.pnc.capstoneprojectandroid.core

data class Product(
    val id: String,
    val name: String,
    val price: String,
    val stockStatus: String
)


object MockData {
    val sampleRecentlyViewed = listOf(
        Product("1", "Safety Glasses", "$14.99", "In Stock"),
        Product("2", "Work Gloves", "$24.99", "In Stock"),
        Product("3", "Measuring Tape", "$9.99", "Unavailable"),
        Product("4", "Cordless Drill", "$129.99", "Low Stock")
    )
}