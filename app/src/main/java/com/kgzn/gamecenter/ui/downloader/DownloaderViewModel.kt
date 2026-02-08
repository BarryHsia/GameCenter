package com.kgzn.gamecenter.ui.downloader

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kgzn.gamecenter.data.AppApi
import com.kgzn.gamecenter.feature.downloader.DownloadManager
import com.kgzn.gamecenter.feature.downloader.downloaditem.DownloadStatus
import com.kgzn.gamecenter.feature.downloader.monitor.CompletedDownloadItemState
import com.kgzn.gamecenter.feature.downloader.monitor.IDownloadItemState
import com.kgzn.gamecenter.feature.downloader.monitor.IDownloadMonitor
import com.kgzn.gamecenter.feature.downloader.monitor.ProcessingDownloadItemState
import com.kgzn.gamecenter.feature.downloader.utils.combineStateFlows
import com.kgzn.gamecenter.feature.downloader.utils.mapStateFlow
import com.kgzn.gamecenter.feature.installer.InstallItemState
import com.kgzn.gamecenter.feature.installer.InstallManager
import com.kgzn.gamecenter.ui.downloader.component.UiDownloadItemState
import com.kgzn.gamecenter.ui.downloader.component.UiDownloadState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

private const val TAG = "DownloaderViewModel"

class DownloaderViewModel(
    downloadMonitor: IDownloadMonitor,
    val downloadManager: DownloadManager,
    val installManager: InstallManager,
    val appApi: AppApi,
) : ViewModel() {

    val downloadStateList: StateFlow<List<IDownloadItemState>> = downloadMonitor.downloadListFlow.mapStateFlow {
        it.sortedByDescending { it.dateAdded }
    }

    val installStateList: StateFlow<List<InstallItemState>> = installManager.installListFlow

    val uiDownloadStateList: StateFlow<List<UiDownloadItemState>> =
        combineStateFlows(downloadStateList, installStateList) { downloadList, installList ->
            downloadList.map { state ->

                fun IDownloadItemState.getDownloadedSize(): Long {
                    return when (this) {
                        is ProcessingDownloadItemState -> progress
                        is CompletedDownloadItemState -> contentLength
                    }
                }

                fun IDownloadItemState.getUiDownloadState(): UiDownloadState {
                    val installItemState = installList.firstOrNull {
                        it.foreignKey == id
                    }
                    return if (installItemState?.isInstalling() == true) {
                        UiDownloadState.Installing
                    } else if (installItemState?.isSuccess()?.not() == true) {
                        UiDownloadState.InstallError
                    } else {
                        when (this) {
                            is ProcessingDownloadItemState -> when (status.asDownloadStatus()) {
                                DownloadStatus.Error -> UiDownloadState.DownloadError
                                DownloadStatus.Added -> UiDownloadState.Paused
                                DownloadStatus.Paused -> UiDownloadState.Paused
                                DownloadStatus.Downloading -> UiDownloadState.Downloading
                                DownloadStatus.Completed -> UiDownloadState.Completed
                            }

                            is CompletedDownloadItemState -> UiDownloadState.Completed
                        }
                    }
                }

                UiDownloadItemState(
                    title = state.label ?: state.name,
                    img = state.imgUrl,
                    totalSize = state.contentLength,
                    downloadedSize = state.getDownloadedSize(),
                    state = state.getUiDownloadState(),
                )
            }
        }

    fun deleteDownloadItem(index: Int) {
        viewModelScope.launch {
            downloadManager.deleteDownload(downloadStateList.value[index].id, { true })
        }
    }

    fun installDownloadItem(index: Int) {
        viewModelScope.launch {
            val id = downloadStateList.value[index].id
            downloadManager.dlListDb.getById(id)?.let { installManager.install(it) }
        }
    }

    fun resumeDownloadItem(index: Int) {
        appApi.getDownloadUrl(downloadStateList.value[index]).catch {
            Log.e(TAG, "resumeDownloadItem: getDownloadUrl error", it)
        }.onEach { url ->
            downloadManager.updateDownloadItem(downloadStateList.value[index].id) {
                it.link = url
            }
            downloadManager.resume(downloadStateList.value[index].id)
        }.launchIn(viewModelScope)
    }


    fun pauseDownloadItem(index: Int) {
        viewModelScope.launch {
            downloadManager.pause(downloadStateList.value[index].id)
        }
    }

}

