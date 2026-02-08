package com.kgzn.gamecenter.ui

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.kgzn.gamecenter.R
import com.kgzn.gamecenter.designsystem.component.ConfirmDialog
import com.kgzn.gamecenter.designsystem.theme.GcTextStyle
import com.kgzn.gamecenter.ui.about.aboutScreen
import com.kgzn.gamecenter.ui.downloader.downloaderScreen
import com.kgzn.gamecenter.ui.gamedetails.gameDetailsScreen
import com.kgzn.gamecenter.ui.home.HomeRoute
import com.kgzn.gamecenter.ui.home.homeScreen
import com.kgzn.gamecenter.ui.input.inputScreen
import com.kgzn.gamecenter.ui.search.searchScreen
import com.kgzn.gamecenter.ui.settings.settingsScreen
import com.kgzn.gamecenter.ui.uninstaller.uninstallerScreen
import com.kgzn.gamecenter.ui.web.webScreen

private const val TAG = "GcApp"


@Composable
fun GcApp(
    modifier: Modifier = Modifier,
    appState: GcAppState,
) {
    val activity = LocalActivity.current
    var showExitDialog by rememberSaveable { mutableStateOf(false) }
    val navBackStackEntry by appState.navController.currentBackStackEntryAsState()

    BackHandler(navBackStackEntry?.destination?.hasRoute<HomeRoute>() == true) {
        showExitDialog = true
    }
    GcApp(
        appState = appState,
        modifier = modifier,
        showExitDialog = showExitDialog,
        onExitDialogDismiss = { showExitDialog = false },
        onExitDialogConfirm = {
            showExitDialog = false
            activity?.finish()
        },
    )
}

@Composable
fun GcApp(
    appState: GcAppState,
    modifier: Modifier = Modifier,
    showExitDialog: Boolean = false,
    onExitDialogDismiss: () -> Unit = {},
    onExitDialogConfirm: () -> Unit = {},
) {
    val navController = appState.navController
    Surface(modifier = modifier.fillMaxSize()) {
        CompositionLocalProvider(LocalNavController provides navController) {
            GcNavHost(appState = appState)
        }
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            SnackbarHost(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = (-50).dp),
                hostState = appState.snackbarHostState,
            ) { data ->
                Text(
                    modifier = Modifier
                        .background(Color(0xFF292929), shape = RoundedCornerShape(5.dp))
                        .padding(vertical = 11.25.dp, horizontal = 35.5.dp)
                        .basicMarquee(),
                    text = data.visuals.message,
                    style = GcTextStyle.Style3,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }


    if (showExitDialog) {
        ConfirmDialog(
            title = stringResource(R.string.exit),
            message = stringResource(R.string.exit_confirm),
            onDismissRequest = onExitDialogDismiss,
            onConfirmRequest = onExitDialogConfirm,
            confirmText = stringResource(R.string.confirm),
            dismissText = stringResource(R.string.cancel),
        )
    }
}


@Composable
fun GcNavHost(
    appState: GcAppState,
    modifier: Modifier = Modifier,
) {
    val navController = appState.navController
    NavHost(navController = navController, startDestination = appState.startDestination, modifier = modifier) {
        homeScreen(appState = appState)
        downloaderScreen(
            downloadMonitor = appState.downloadMonitor,
            downloadManager = appState.downloadManager,
            installManager = appState.installManager,
            appApi = appState.appApi,
        )
        uninstallerScreen(installManager = appState.installManager)
        aboutScreen()
        searchScreen(appState = appState)
        inputScreen()
        gameDetailsScreen(appState = appState)
        webScreen(appState = appState)
        settingsScreen(appState = appState)
    }
}

val LocalNavController = compositionLocalOf<NavHostController> {
    error("No NavHostController provided")
}