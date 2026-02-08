package com.kgzn.gamecenter.ui.downloader

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import com.kgzn.gamecenter.data.AppApi
import com.kgzn.gamecenter.feature.downloader.DownloadManager
import com.kgzn.gamecenter.feature.downloader.monitor.IDownloadMonitor
import com.kgzn.gamecenter.feature.installer.InstallManager
import kotlinx.serialization.Serializable

@Serializable
object DownloaderRoute

fun NavController.navigateToDownloader(navOptions: NavOptionsBuilder.() -> Unit = {}) {
    navigate(DownloaderRoute, navOptions)
}

fun NavGraphBuilder.downloaderScreen(
    downloadMonitor: IDownloadMonitor,
    downloadManager: DownloadManager,
    installManager: InstallManager,
    appApi: AppApi,
) {
    composable<DownloaderRoute> {
        DownloaderScreen(
            downloadMonitor = downloadMonitor,
            downloadManager = downloadManager,
            installManager = installManager,
            appApi = appApi,
        )
    }
}