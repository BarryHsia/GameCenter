package com.kgzn.gamecenter.ui.search

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import com.kgzn.gamecenter.ui.GcAppState
import kotlinx.serialization.Serializable

@Serializable
object SearchRoute

fun NavController.navigateToSearch(navOptions: NavOptionsBuilder.() -> Unit = {}) {
    navigate(SearchRoute, navOptions)
}

fun NavGraphBuilder.searchScreen(appState: GcAppState) {
    composable<SearchRoute> {
        SearchScreen(appState = appState)
    }
}