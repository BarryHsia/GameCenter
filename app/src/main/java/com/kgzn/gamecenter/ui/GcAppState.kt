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
import com.kgzn.gamecenter.db.playrecord.PlayRecord
import com.kgzn.gamecenter.db.playrecord.PlayRecordDao
import com.kgzn.gamecenter.feature.network.NetworkMonitor
import com.kgzn.gamecenter.ui.home.HomeRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@Composable
fun rememberGcAppState(
    navController: NavHostController = rememberNavController(),
    networkMonitor: NetworkMonitor,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    playRecordDao: PlayRecordDao,
    startDestination: Any = HomeRoute,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
): GcAppState {
    return remember(
        navController,
        networkMonitor,
        coroutineScope,
        playRecordDao,
        startDestination,
    ) {
        GcAppState(
            navController = navController,
            networkMonitor = networkMonitor,
            coroutineScope = coroutineScope,
            playRecordDao = playRecordDao,
            startDestination = startDestination,
            snackbarHostState = snackbarHostState,
        )
    }
}

val LocalGcAppState = compositionLocalOf<GcAppState> {
    error("LocalGcAppState not provided")
}

@Stable
class GcAppState(
    val navController: NavHostController,
    networkMonitor: NetworkMonitor,
    coroutineScope: CoroutineScope,
    playRecordDao: PlayRecordDao,
    val startDestination: Any,
    val snackbarHostState: SnackbarHostState,
) {
    private val previousDestination = mutableStateOf<NavDestination?>(null)

    val currentDestination: NavDestination?
        @Composable get() {
            val currentEntry = navController.currentBackStackEntryFlow
                .collectAsState(initial = null)
            return currentEntry.value?.destination.also { destination ->
                if (destination != null) {
                    previousDestination.value = destination
                }
            } ?: previousDestination.value
        }

    val isOffline: StateFlow<Boolean> = networkMonitor.isOnline
        .map(Boolean::not)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    val isOnline: StateFlow<Boolean> = networkMonitor.isOnline
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    val playRecords: StateFlow<List<PlayRecord>> = playRecordDao.getAllByLastPlayTimeDesc()
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )
}
