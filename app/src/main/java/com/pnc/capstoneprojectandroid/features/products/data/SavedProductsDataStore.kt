package com.pnc.capstoneprojectandroid.features.products.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.savedProductsDataStore by preferencesDataStore(name = "saved_products")

@Singleton
class SavedProductsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val key = stringSetPreferencesKey("saved_ids")

    val savedIds: Flow<Set<Int>> = context.savedProductsDataStore.data
        .map { prefs -> prefs[key]?.mapNotNull { it.toIntOrNull() }?.toSet() ?: emptySet() }

    suspend fun toggle(productId: Int) {
        context.savedProductsDataStore.edit { prefs ->
            val current = prefs[key] ?: emptySet()
            val id = productId.toString()
            prefs[key] = if (id in current) current - id else current + id
        }
    }
}

