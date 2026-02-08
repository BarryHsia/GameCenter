package com.kgzn.gamecenter.ui.web

import android.net.Uri
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

@Serializable
data class WebRoute(
    val url: String,
)

fun NavGraphBuilder.webScreen(navController: NavHostController) {
    composable<WebRoute> {
        val route = it.toRoute<WebRoute>()
        WebScreen(
            route.url,
            navHostController = navController
        )
    }
}

fun Uri.isWebUri(): Boolean {
    return scheme == "https" || scheme == "http"
}
