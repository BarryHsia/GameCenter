package com.kgzn.gamecenter.ui.downloader

import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.material3.Icon
import androidx.tv.material3.Text
import com.kgzn.gamecenter.R
import com.kgzn.gamecenter.data.AppApi
import com.kgzn.gamecenter.designsystem.component.GcTopAppBar
import com.kgzn.gamecenter.designsystem.theme.GcTextStyle
import com.kgzn.gamecenter.feature.downloader.DownloadManager
import com.kgzn.gamecenter.feature.downloader.monitor.IDownloadMonitor
import com.kgzn.gamecenter.feature.installer.InstallManager
import com.kgzn.gamecenter.ui.downloader.component.DeleteDialog
import com.kgzn.gamecenter.ui.downloader.component.EmptyBackground
import com.kgzn.gamecenter.ui.downloader.component.UiDownloadItem
import com.kgzn.gamecenter.ui.downloader.component.UiDownloadItemState
import com.kgzn.gamecenter.ui.downloader.component.UiDownloadState

private const val TAG = "DownloaderScreen"

@Composable
fun DownloaderScreen(
    downloadMonitor: IDownloadMonitor,
    downloadManager: DownloadManager,
    installManager: InstallManager,
    appApi: AppApi,
) {

    val viewModel = viewModel {
        DownloaderViewModel(
            downloadMonitor = downloadMonitor,
            downloadManager = downloadManager,
            installManager = installManager,
            appApi = appApi,
        )
    }


    val uiDownloadItemStates by viewModel.uiDownloadStateList.collectAsState()

    var focusedIndex by remember { mutableIntStateOf(-1) }
    var showDeleteDialog by remember { mutableIntStateOf(0) }

    if (showDeleteDialog > 0) {
        DeleteDialog(
            onDismiss = { showDeleteDialog-- },
            onConfirm = {
                viewModel.deleteDownloadItem(focusedIndex)
                showDeleteDialog--
            },
        )
    }

    DownloaderScreen(
        downloadList = uiDownloadItemStates,
        onClick = { index ->
            when (uiDownloadItemStates[index].state) {
                UiDownloadState.Paused -> viewModel.resumeDownloadItem(index)
                UiDownloadState.Completed -> viewModel.installDownloadItem(index)
                UiDownloadState.Downloading -> viewModel.pauseDownloadItem(index)
                UiDownloadState.Installing -> Unit
                UiDownloadState.DownloadError -> viewModel.resumeDownloadItem(index)
                UiDownloadState.InstallError -> viewModel.installDownloadItem(index)
                UiDownloadState.Installed -> Unit
            }
        },
        onLongClick = { index ->
            focusedIndex = index
            showDeleteDialog = 1
        },
    )
}

@Composable
fun DownloaderScreen(
    modifier: Modifier = Modifier,
    downloadList: List<UiDownloadItemState>,
    onFocused: (Int) -> Unit = {},
    onClick: (Int) -> Unit = {},
    onLongClick: (Int) -> Unit = {},
) {
    Column(
        modifier = modifier,
    ) {
        Box {
            GcTopAppBar(title = stringResource(R.string.download_manager), trailingContent = {
                Text(
                    text = buildAnnotatedString {
                        val originalText = stringResource(R.string.delete_download_record_tip, "##icon##")
                        originalText.split("##icon##").forEachIndexed { index, string ->
                            if (index == 0) {
                                append(string)
                            } else {
                                appendInlineContent("##icon##")
                                append(string)
                            }
                        }
                    },
                    inlineContent = mapOf(
                        "##icon##" to InlineTextContent(
                            placeholder = Placeholder(
                                width = 32.sp,
                                height = 32.sp,
                                placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
                            ),
                            children = {
                                Icon(
                                    painter = painterResource(R.drawable.ic_key_ok),
                                    contentDescription = it,
                                    tint = Color.White.copy(alpha = 0.6f),
                                )
                            }
                        )
                    ),
                    style = GcTextStyle.Style4,
                    maxLines = 1,
                )
            })
            if (downloadList.isEmpty()) {
                EmptyBackground(text = stringResource(R.string.no_download_record))
            }
        }
        if (downloadList.isNotEmpty()) {
            LazyVerticalGrid(
                modifier = Modifier.focusGroup(),
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(horizontal = 42.5.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(25.dp),
                horizontalArrangement = Arrangement.spacedBy(25.dp)
            ) {
                itemsIndexed(downloadList) { index, download ->
                    UiDownloadItem(
                        modifier = Modifier.onFocusChanged {
                            if (it.isFocused) {
                                onFocused(index)
                            }
                        },
                        state = download,
                        onClick = { onClick(index) },
                        onLongClick = { onLongClick(index) },
                    )
                }
            }
        }
    }
}

@Preview(device = "spec:width=960dp,height=540dp,dpi=320")
@Composable
fun DownloaderScreenPreview() {
    DownloaderScreen(
        downloadList = listOf(
            UiDownloadItemState(
                title = "Game 1",
                img = R.drawable.ic_controller_game,
                totalSize = 1000000,
                downloadedSize = 500000,
                state = UiDownloadState.Downloading,
            ),
            UiDownloadItemState(
                title = "Game 2",
                img = R.drawable.ic_gamepads,
                totalSize = 2000000,
                downloadedSize = 1000000,
                state = UiDownloadState.Paused,
            ),
            UiDownloadItemState(
                title = "Game 3",
                img = "",
                totalSize = 2000000,
                downloadedSize = 1000000,
                state = UiDownloadState.Paused,
            ),
        )
    )
}
