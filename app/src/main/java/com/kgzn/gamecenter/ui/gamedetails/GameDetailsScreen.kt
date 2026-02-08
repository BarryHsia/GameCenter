package com.kgzn.gamecenter.ui.gamedetails

import androidx.compose.foundation.Image
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component1
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component2
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component3
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component4
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.material3.Text
import com.kgzn.gamecenter.R
import com.kgzn.gamecenter.designsystem.theme.GcTextStyle
import com.kgzn.gamecenter.ui.GcAppState
import com.kgzn.gamecenter.ui.gamedetails.component.ButtonBar
import com.kgzn.gamecenter.ui.gamedetails.component.DescDialog
import com.kgzn.gamecenter.ui.gamedetails.component.GameDetailsBackground
import com.kgzn.gamecenter.ui.gamedetails.component.GooglePlayDialog
import com.kgzn.gamecenter.ui.gamedetails.component.InfoSector
import com.kgzn.gamecenter.ui.gamedetails.component.RelativeResourceSector
import com.kgzn.gamecenter.ui.gamedetails.component.SupportTypeBar
import com.kgzn.gamecenter.ui.home.component.Loading


@Composable
fun GameDetailsScreen(
    route: GameDetailsRoute,
    appState: GcAppState,
) {

    val navController = appState.navController
    val context = LocalContext.current
    val cacheDir = context.cacheDir
    val viewModel = viewModel {
        GameDetailsViewModel(
            appApi = appState.appApi,
            param = route,
            downloadManager = appState.downloadManager,
            folder = cacheDir.path,
            navController = appState.navController,
            playRecordDao = appState.playRecordDao,
            downloadMonitor = appState.downloadMonitor,
            packageManager = context.packageManager,
            installManager = appState.installManager,
            snackbarHostState = appState.snackbarHostState,
        )
    }

    val loading by viewModel.loading.collectAsState()
    val isEmpty by viewModel.isEmpty.collectAsState()
    val label by viewModel.label.collectAsState()
    val desc by viewModel.desc.collectAsState()
    val tags by viewModel.tags.collectAsState()
    val bgUrl by viewModel.bgUrl.collectAsState()
    val controlTypes by viewModel.controlTypes.collectAsState()
    val relativeResources by viewModel.relativeResources.collectAsState()
    val gameType by viewModel.gameType.collectAsState()
    val percent by viewModel.percent.collectAsState()
    val isPaused by viewModel.isPaused.collectAsState()
    val isDownloading by viewModel.isDownloading.collectAsState()
    val isCompleted by viewModel.isCompleted.collectAsState()
    val isInstalled by viewModel.isInstalled.collectAsState()
    val hasUpdate by viewModel.hasUpdate.collectAsState()
    val isInstalling by viewModel.isInstalling.collectAsState()
    val isDownloadError by viewModel.isDownloadError.collectAsState()
    val isInstallError by viewModel.isInstallError.collectAsState()
    val showGooglePlayDialog by viewModel.showGooglePlayDialog.collectAsState()

    val isOffline by appState.isOffline.collectAsStateWithLifecycle()
    LaunchedEffect(isOffline) {
        viewModel.fetchInfo()
    }

    if (loading) {
        Box(Modifier.fillMaxSize()) {
            Loading(
                modifier = Modifier
                    .size(60.dp)
                    .align(Alignment.Center),
            )
        }
        return
    } else if (isEmpty) {
        Box(Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    modifier = Modifier.size(125.dp),
                    painter = painterResource(R.drawable.box_not_network),
                    contentDescription = null
                )
                Text(stringResource(R.string.network_error), style = GcTextStyle.Style4)
            }
        }
        return
    }

    if (showGooglePlayDialog) {
        GooglePlayDialog(
            onDismissRequest = { viewModel.dismissGooglePlayDialog() },
            onConfirmRequest = { viewModel.downloadGooglePlayApp(context) },
        )
    }

    GameDetailsScreen(
        title = { label },
        tags = { tags },
        desc = { desc },
        bgUrl = { bgUrl },
        onPositiveClick = {
            when (gameType) {
                GameDetailsType.APK -> {
                    if (isInstalling) {
                        Unit
                    } else if (!isInstalled || hasUpdate) {
                        if (isPaused || isDownloadError) viewModel.resumeDownload()
                        else if (isDownloading) viewModel.pauseDownload()
                        else if (isCompleted || isInstallError) viewModel.installApk(context)
                        else viewModel.download(context)
                    } else {
                        viewModel.openApp(context)
                    }
                }

                GameDetailsType.H5 -> viewModel.playH5(context)
                GameDetailsType.UNKNOW -> {}
            }
        },
        onNegativeClick = viewModel::cancelDownload,
        showNegativeButton = {
            when (gameType) {
                GameDetailsType.APK -> isPaused || isDownloading
                GameDetailsType.H5 -> false
                GameDetailsType.UNKNOW -> false
            }
        },
        positiveButtonText = when (gameType) {
            GameDetailsType.APK -> {
                if (isInstalling) stringResource(R.string.installing)
                else if (isDownloadError) stringResource(R.string.retry)
                else if (!isInstalled || hasUpdate) {
                    if (isPaused) stringResource(R.string.resume)
                    else if (isDownloading) "${(percent ?: 0)}%"
                    else if (isCompleted) stringResource(R.string.install)
                    else if (hasUpdate) stringResource(R.string.update) else stringResource(R.string.download)
                } else stringResource(R.string.play)
            }

            GameDetailsType.H5 -> stringResource(R.string.play)
            GameDetailsType.UNKNOW -> "WTF"
        },
        negativeButtonText = stringResource(R.string.cancel),
        progress = { (percent ?: 100) / 100f },
        supportTypes = { controlTypes },
        relativeResources = { relativeResources.map { it.title to it.imgUrl } },
        onResourceClick = {
            relativeResources.getOrNull(it)?.let { resource ->
                navController.navigate(GameDetailsRoute(resource))
            }
        }
    )
}

@Composable
fun GameDetailsScreen(
    modifier: Modifier = Modifier,
    title: () -> String,
    desc: () -> String = { "" },
    tags: () -> List<String> = { emptyList() },
    bgUrl: () -> String? = { null },
    onPositiveClick: () -> Unit = {},
    onNegativeClick: () -> Unit = {},
    showNegativeButton: () -> Boolean = { false },
    positiveButtonText: String = "",
    negativeButtonText: String = "",
    progress: () -> Float = { 1f },
    supportTypes: () -> List<String> = { emptyList() },
    relativeResources: () -> List<Pair<String, Any?>> = { emptyList() },
    onResourceClick: (Int) -> Unit = {},
) {

    var descExpended by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        GameDetailsBackground(
            modifier = Modifier
                .fillMaxSize()
                .then(if (descExpended) Modifier.blur(10.dp) else Modifier),
            model = bgUrl(),
        )

        val (container, infoRequester, buttonRequester, resourcesRequest) = remember { FocusRequester.createRefs() }

        LaunchedEffect(Unit) {
            if (!container.restoreFocusedChild()) {
                container.requestFocus()
            }
        }

        Column(
            modifier = Modifier
                .then(if (descExpended) Modifier.blur(10.dp) else Modifier)
                .align(Alignment.BottomStart)
                .focusRequester(container)
                .focusRestorer(buttonRequester)
                .focusGroup(),
            verticalArrangement = Arrangement.Bottom,
        ) {
            InfoSector(
                modifier = Modifier
                    .focusRequester(infoRequester)
                    .focusProperties {
                        down = buttonRequester
                    },
                title = title,
                desc = desc,
                tags = tags,
                onDescClick = { descExpended = true },
            )
            Row(
                modifier = Modifier
                    .padding(horizontal = 25.dp)
                    .padding(top = 25.5.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ButtonBar(
                    modifier = Modifier.focusRequester(buttonRequester),
                    onPositiveClick = onPositiveClick,
                    onNegativeClick = onNegativeClick,
                    showNegativeButton = showNegativeButton,
                    positiveButtonText = positiveButtonText,
                    negativeButtonText = negativeButtonText,
                    progress = progress,
                )
                SupportTypeBar(supportTypes = supportTypes)
            }
            RelativeResourceSector(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .focusRequester(resourcesRequest),
                resources = relativeResources,
                onResourceClick = {
                    container.saveFocusedChild()
                    onResourceClick(it)
                },
            )
        }

        if (descExpended) {
            DescDialog(
                modifier = Modifier.padding(vertical = 25.dp),
                desc = desc,
                onDismissRequest = { descExpended = false },
            )
        }
    }
}

@Preview(device = "spec:width=960dp,height=540dp,dpi=320")
@Composable
fun GameDetailsScreenPreview() {

}
