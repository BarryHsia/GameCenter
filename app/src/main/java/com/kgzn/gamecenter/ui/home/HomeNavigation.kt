package com.kgzn.gamecenter.ui.home

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.kgzn.gamecenter.ui.GcAppState
import kotlinx.serialization.Serializable

@Serializable
object HomeRoute

fun NavGraphBuilder.homeScreen(
    appState: GcAppState,
) {
    composable<HomeRoute> {
        HomeScreen(appState = appState)
    }
}
