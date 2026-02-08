package com.kgzn.gamecenter.ui.home

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component1
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component2
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kgzn.gamecenter.ui.GcAppState
import com.kgzn.gamecenter.ui.LocalNavController
import com.kgzn.gamecenter.ui.about.navigateToAbout
import com.kgzn.gamecenter.ui.downloader.navigateToDownloader
import com.kgzn.gamecenter.ui.gamedetails.GameDetailsRoute
import com.kgzn.gamecenter.ui.home.component.HomeBar
import com.kgzn.gamecenter.ui.home.component.Loading
import com.kgzn.gamecenter.ui.home.page.ContentPage
import com.kgzn.gamecenter.ui.home.page.MinePage
import com.kgzn.gamecenter.ui.input.navigateInput
import com.kgzn.gamecenter.ui.search.navigateToSearch
import com.kgzn.gamecenter.ui.settings.navigateToSettings
import com.kgzn.gamecenter.ui.uninstaller.navigateToUninstaller
import kotlinx.coroutines.launch

const val TAG = "HomeScreen"

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun HomeScreen(
    appState: GcAppState,
) {

    val context = LocalContext.current
    val homeViewModel: HomeViewModel = viewModel {
        HomeViewModel(
            appState.appApi,
            appState,
            context = context
        )
    }
    val contentConfigs by homeViewModel.contentConfigs.collectAsState()
    val loading by homeViewModel.loading.collectAsState()
    val pagerState by homeViewModel.pagerState.collectAsState()
    val actions by homeViewModel.actions.collectAsState()
    val focusRequesters = remember(contentConfigs) {
        List(contentConfigs.size + 1) { FocusRequester() }
    }

    val (barRequest, contentRequester) = remember { FocusRequester.createRefs() }
    val navHostController = LocalNavController.current
    var barExpand by remember { mutableStateOf(false) }
    val barBlurRadius by animateDpAsState(if (barExpand) 10.dp else 0.dp)
    val barColor by animateColorAsState(if (barExpand) Color(0xFF121212).copy(0.5f) else Color.Transparent)
    val isOffline by appState.isOffline.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    LaunchedEffect(isOffline) {
        if (!isOffline) {
            homeViewModel.fetchContentConfigs()
        }
    }
    BackHandler(barExpand) {
        contentRequester.requestFocus()
    }

    Box(
        modifier = Modifier
            .focusRestorer(contentRequester)
            .focusGroup()
    ) {
        HomeBar(
            modifier = Modifier
                .zIndex(1f)
                .focusRequester(barRequest)
                .focusProperties {
                    end = contentRequester
                },
            actionsBuilder = { actions },
            selectedActionIndex = pagerState.currentPage,
            onSearchClick = navHostController::navigateToSearch,
            onActionClick = { index ->
                scope.launch {
                    pagerState.scrollToPage(index)
                    focusRequesters[index].requestFocus()
                }
            },
            onExpandChange = { expanded -> barExpand = expanded },
        )
        if (loading) {
            Loading(
                modifier = Modifier
                    .size(60.dp)
                    .align(Alignment.Center)
                    .focusRequester(contentRequester)
                    .focusable(),
            )
        } else {
            VerticalPager(
                modifier = Modifier
                    .blur(barBlurRadius)
                    .padding(start = 59.5.dp)
                    .fillMaxSize()
                    .focusRequester(contentRequester)
                    .drawWithContent {
                        drawContent()
                        drawRect(barColor)
                    },
                state = pagerState,
                userScrollEnabled = false,
            ) { index ->
                Box(
                    modifier = Modifier
                        .focusRequester(focusRequesters[index])
                        .focusProperties {
                            onEnter = {
                                if (pagerState.targetPage != index) {
                                    if (requestedFocusDirection == FocusDirection.Enter) {
                                        focusRequesters[pagerState.targetPage].requestFocus()
                                    }
                                    cancelFocusChange()
                                }
                            }
                        }
                        .focusRestorer()
                        .focusGroup(),
                ) {
                    if (index == 0) {
                        val playRecords by appState.playRecords.collectAsStateWithLifecycle()
                        MinePage(
                            onGamepadsClick = navHostController::navigateInput,
                            onDownloaderClick = navHostController::navigateToDownloader,
                            onUninstallerClick = navHostController::navigateToUninstaller,
                            onSettingsClick = navHostController::navigateToSettings,
                            onAboutClick = navHostController::navigateToAbout,
                            playRecords = playRecords,
                            onRecordClick = { index ->
                                appState.navController.navigate(GameDetailsRoute(playRecords[index]))
                            },
                        )
                    } else {
                        ContentPage(
                            contentConfigs[index - 1],
                            navHostController = navHostController,
                        )
                    }
                }
            }
        }
    }
}