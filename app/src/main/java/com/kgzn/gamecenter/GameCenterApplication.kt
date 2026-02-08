package com.kgzn.gamecenter

import android.app.Application
import android.util.Log
import com.kgzn.eventreportsdk.EventSDK
import com.kgzn.gamecenter.data.AppApi
import com.kgzn.gamecenter.feature.downloader.DownloadManager
import com.kgzn.gamecenter.feature.downloader.DownloadManagerEvents
import com.kgzn.gamecenter.feature.downloader.downloaditem.DownloadStatus
import com.kgzn.gamecenter.feature.installer.InstallEvents
import com.kgzn.gamecenter.feature.installer.InstallManager
import com.kgzn.gamecenter.feature.settings.SettingsManager
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class GameCenterApplication : Application() {

    companion object {
        const val TAG = "GameCenterApplication"
    }

    private val scope by lazy { CoroutineScope(SupervisorJob()) }

    @Inject
    lateinit var downloadManager: DownloadManager

    @Inject
    lateinit var installManager: InstallManager

    @Inject
    lateinit var settingsManager: SettingsManager

    @Inject
    lateinit var appApi: AppApi

    override fun onCreate() {
        Log.d(TAG, "onCreate")
        super.onCreate()
        scope.launch {
            downloadManager.boot()
            downloadManager.getDownloadList().forEach { downloadItem ->
                if (downloadItem.status == DownloadStatus.Downloading) {
                    appApi.getDownloadUrl(downloadItem).catch {
                        Log.e(TAG, "onCreate: getDownloadUrl error", it)
                    }.onEach { url ->
                        downloadManager.updateDownloadItem(downloadItem.id) {
                            it.link = url
                        }
                        downloadManager.resume(downloadItem.id)
                    }.launchIn(scope)
                }
            }
        }

        installManager.installEvents.filter {
            it is InstallEvents.OnPackageInstalled
        }.onEach { events ->
            Log.d(TAG, "onCreate: $events")
            if (events is InstallEvents.OnPackageInstalled) {
                val packageName = events.packageName
                if (packageName != null && settingsManager.isClearPackageAfterInstall.first()) {
                    val packageInfo = packageManager.getPackageInfo(packageName, 0)
                    val longVersionCode = packageInfo.longVersionCode
                    downloadManager.dlListDb.getByPackage(packageName, longVersionCode.toInt())
                        .firstOrNull()?.let { downloadItem ->
                            Log.d(TAG, "onCreate: delete downloadItem $downloadItem")
                            downloadManager.deleteDownload(downloadItem.id, { true })
                        }
                }
            }
        }.launchIn(scope)

        settingsManager.isAutoInstall.distinctUntilChanged().onEach {
            if (it) {
                startAutoInstall()
            } else {
                stopAutoInstall()
            }
        }.launchIn(scope)

        val startedJobIds = mutableListOf<Long>()
        downloadManager.listOfJobsEvents.onEach { events ->
            Log.d(TAG, "onCreate: $events")

            when (events) {
                is DownloadManagerEvents.OnJobCompleted,
                is DownloadManagerEvents.OnJobCanceled -> startedJobIds.removeIf { it == events.downloadItem.id }


                is DownloadManagerEvents.OnJobStarted -> {
                    if (startedJobIds.size == 5) {
                        downloadManager.pause(startedJobIds.first())
                        startedJobIds.remove(startedJobIds.first())
                    }
                    startedJobIds.add(events.downloadItem.id)
                }

                else -> Unit
            }
        }.launchIn(scope)

        initEventReportSdk()
    }

    private var autoInstallJob: Job? = null

    fun startAutoInstall() {
        Log.d(TAG, "startAutoInstall")
        autoInstallJob?.cancel()
        autoInstallJob = downloadManager.listOfJobsEvents.filter {
            it is DownloadManagerEvents.OnJobCompleted
        }.onEach { events ->
            installManager.install(events.downloadItem)
        }.launchIn(scope)
    }

    fun stopAutoInstall() {
        Log.d(TAG, "stopAutoInstall")
        autoInstallJob?.cancel()
        autoInstallJob = null
    }

    private fun initEventReportSdk() {
        EventSDK.getInstance().init(this)
    }
}