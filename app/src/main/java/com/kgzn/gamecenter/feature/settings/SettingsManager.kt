package com.kgzn.gamecenter.feature.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsManager(val context: Context) {

    companion object {
        val AutoInstall = booleanPreferencesKey("auto_install")
        val ClearPackageAfterInstall = booleanPreferencesKey("clear_package_after_install")
    }

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    private val dataStore: DataStore<Preferences> = context.dataStore

    val isAutoInstall: Flow<Boolean> = dataStore.data.map { it[AutoInstall] ?: true }

    suspend fun setAutoInstall(autoInstall: Boolean) {
        dataStore.edit { preferences ->
            preferences[AutoInstall] = autoInstall
        }
    }

    val isClearPackageAfterInstall: Flow<Boolean> = dataStore.data.map { it[ClearPackageAfterInstall] ?: true }

    suspend fun setClearPackageAfterInstall(clearPackageAfterInstall: Boolean) {
        dataStore.edit { preferences ->
            preferences[ClearPackageAfterInstall] = clearPackageAfterInstall
        }
    }
}
