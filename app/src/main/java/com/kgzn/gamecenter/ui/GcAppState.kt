package com.kgzn.gamecenter.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.kgzn.gamecenter.data.AppApi
import com.kgzn.gamecenter.db.playrecord.PlayRecordDao
import com.kgzn.gamecenter.feature.downloader.DownloadManager
import com.kgzn.gamecenter.feature.downloader.monitor.IDownloadMonitor
import com.kgzn.gamecenter.feature.installer.InstallManager
import com.kgzn.gamecenter.feature.network.NetworkMonitor
import com.kgzn.gamecenter.feature.settings.SettingsManager
import com.kgzn.gamecenter.ui.home.HomeRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@Composable
fun rememberGcAppState(
    appApi: AppApi,
    navController: NavHostController = rememberNavController(),
    downloadMonitor: IDownloadMonitor,
    downloadManager: DownloadManager,
    networkMonitor: NetworkMonitor,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    playRecordDao: PlayRecordDao,
    installManager: InstallManager,
    settingsManager: SettingsManager,
    startDestination: Any = HomeRoute,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
): GcAppState {
    return remember(
        appApi,
        navController,
        downloadMonitor,
        downloadManager,
        networkMonitor,
        coroutineScope,
        playRecordDao,
        installManager,
        settingsManager,
        startDestination,
    ) {
        GcAppState(
            appApi,
            navController,
            downloadMonitor,
            downloadManager,
            networkMonitor,
            coroutineScope,
            playRecordDao,
            installManager,
            settingsManager,
            startDestination,
            snackbarHostState,
        )
    }
}

val LocalGcAppState = compositionLocalOf<GcAppState> {
    error("LocalGcAppState not provided")
}

@Stable
class GcAppState(
    val appApi: AppApi,
    val navController: NavHostController,
    val downloadMonitor: IDownloadMonitor,
    val downloadManager: DownloadManager,
    networkMonitor: NetworkMonitor,
    coroutineScope: CoroutineScope,
    val playRecordDao: PlayRecordDao,
    val installManager: InstallManager,
    val settingsManager: SettingsManager,
    val startDestination: Any,
    val snackbarHostState: SnackbarHostState,
) {
    private val previousDestination = mutableStateOf<NavDestination?>(null)

    val currentDestination: NavDestination?
        @Composable get() {
            // Collect the currentBackStackEntryFlow as a state
            val currentEntry = navController.currentBackStackEntryFlow
                .collectAsState(initial = null)

            // Fallback to previousDestination if currentEntry is null
            return currentEntry.value?.destination.also { destination ->
                if (destination != null) {
                    previousDestination.value = destination
                }
            } ?: previousDestination.value
        }

    val isOffline = networkMonitor.isOnline
        .map(Boolean::not)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    val isOnline = networkMonitor.isOnline
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    val playRecords = playRecordDao.getAllByLastPlayTimeDesc()
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )
}