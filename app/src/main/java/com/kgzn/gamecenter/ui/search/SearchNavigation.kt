package com.kgzn.gamecenter.ui.search

import androidx.compose.material3.SnackbarHostState
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object SearchRoute

fun NavController.navigateToSearch(navOptions: NavOptionsBuilder.() -> Unit = {}) {
    navigate(SearchRoute, navOptions)
}

fun NavGraphBuilder.searchScreen(snackbarHostState: SnackbarHostState) {
    composable<SearchRoute> {
        SearchScreen(snackbarHostState = snackbarHostState)
    }
}
