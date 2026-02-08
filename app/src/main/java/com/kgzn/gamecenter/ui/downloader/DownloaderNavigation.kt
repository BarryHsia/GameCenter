package com.kgzn.gamecenter.ui.downloader

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object DownloaderRoute

fun NavController.navigateToDownloader(navOptions: NavOptionsBuilder.() -> Unit = {}) {
    navigate(DownloaderRoute, navOptions)
}

fun NavGraphBuilder.downloaderScreen() {
    composable<DownloaderRoute> {
        DownloaderScreen()
    }
}
