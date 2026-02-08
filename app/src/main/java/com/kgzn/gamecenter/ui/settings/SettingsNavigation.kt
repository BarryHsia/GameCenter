package com.kgzn.gamecenter.ui.settings

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.kgzn.gamecenter.ui.GcAppState
import kotlinx.serialization.Serializable

@Serializable
object SettingsRoute

fun NavController.navigateToSettings() {
    navigate(SettingsRoute)
}

fun NavGraphBuilder.settingsScreen(
    appState: GcAppState,
) {
    composable<SettingsRoute> {
        SettingsScreen(appState)
    }
}
