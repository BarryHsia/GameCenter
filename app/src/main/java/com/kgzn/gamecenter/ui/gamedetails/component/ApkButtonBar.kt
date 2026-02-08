package com.kgzn.gamecenter.ui.gamedetails.component

import android.content.pm.PackageInfo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kgzn.gamecenter.feature.downloader.downloaditem.DownloadStatus
import com.kgzn.gamecenter.feature.downloader.monitor.CompletedDownloadItemState
import com.kgzn.gamecenter.feature.downloader.monitor.IDownloadItemState
import com.kgzn.gamecenter.feature.downloader.monitor.ProcessingDownloadItemState

@Composable
fun ApkButtonBar(
    packageInfo: PackageInfo?,
    downloadItemState: IDownloadItemState?,
    latestVersionCode: Int,
    onDownloadClick: () -> Unit = {},
    onInstallClick: () -> Unit = {},
    onPauseClick: () -> Unit = {},
    onResumeClick: () -> Unit = {},
    onCancelClick: () -> Unit = {},
) {
    val isInstalled = packageInfo != null
    val hasUpdate = isInstalled && packageInfo.longVersionCode < latestVersionCode.toLong()

    val downloadStatus = when (downloadItemState) {
        is ProcessingDownloadItemState -> {
            downloadItemState.status.asDownloadStatus()
        }

        is CompletedDownloadItemState -> {
            DownloadStatus.Completed
        }

        else -> null
    }
    val progress = when (downloadItemState) {
        is ProcessingDownloadItemState -> (downloadItemState.percent ?: 0) / 100f
        is CompletedDownloadItemState -> 1f
        else -> 1f
    }

    Row(
        modifier = Modifier.padding(start = 25.dp, end = 21.dp),
        horizontalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        val buttonText: String = if (!isInstalled || hasUpdate) {
            when (downloadStatus) {
                DownloadStatus.Error -> if (hasUpdate) "Update" else "Download"
                DownloadStatus.Added -> if (hasUpdate) "Update" else "Download"
                DownloadStatus.Paused -> "Paused"
                DownloadStatus.Downloading -> "Downloading"
                DownloadStatus.Completed -> "Install"
                null -> if (hasUpdate) "Update" else "Download"
            }
        } else {
            "Play"
        }
        DownloadButton(
            text = buttonText,
            progress = progress,
            onClick = {
                when (downloadStatus) {
                    DownloadStatus.Error -> onDownloadClick()
                    DownloadStatus.Added -> onDownloadClick()
                    DownloadStatus.Paused -> onResumeClick()
                    DownloadStatus.Downloading -> onPauseClick()
                    DownloadStatus.Completed -> onInstallClick()
                    null -> onDownloadClick()
                }
            }
        )
        if (downloadStatus == DownloadStatus.Paused) {
            DownloadButton(
                text = "Cancel",
                progress = 0f,
                onClick = onCancelClick,
            )
        }
    }
}