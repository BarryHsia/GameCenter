package com.kgzn.gamecenter.ui.gamedetails

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.kgzn.eventreportsdk.EventSDK
import com.kgzn.gamecenter.R
import com.kgzn.gamecenter.data.Info
import com.kgzn.gamecenter.data.repository.GameRepository
import com.kgzn.gamecenter.db.playrecord.PlayRecord
import com.kgzn.gamecenter.db.playrecord.PlayRecordDao
import com.kgzn.gamecenter.feature.downloader.DownloadManager
import com.kgzn.gamecenter.feature.downloader.downloaditem.DownloadItem
import com.kgzn.gamecenter.feature.downloader.downloaditem.DownloadStatus
import com.kgzn.gamecenter.feature.downloader.monitor.IDownloadItemState
import com.kgzn.gamecenter.feature.downloader.monitor.IDownloadMonitor
import com.kgzn.gamecenter.feature.downloader.monitor.ProcessingDownloadItemState
import com.kgzn.gamecenter.feature.downloader.utils.OnDuplicateStrategy
import com.kgzn.gamecenter.feature.downloader.utils.combineStateFlows
import com.kgzn.gamecenter.feature.downloader.utils.mapStateFlow
import com.kgzn.gamecenter.feature.installer.InstallItemState
import com.kgzn.gamecenter.feature.installer.InstallManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameDetailsViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val downloadManager: DownloadManager,
    private val playRecordDao: PlayRecordDao,
    downloadMonitor: IDownloadMonitor,
    private val installManager: InstallManager,
    @ApplicationContext private val appContext: Context,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    companion object {
        private const val TAG = "GameDetailsViewModel"
        private const val GOOGLE_PLAY_PACKAGE_NAME = "com.android.vending"
    }

    private val route = savedStateHandle.toRoute<GameDetailsRoute>()
    private val param = route
    private val folder = appContext.cacheDir.path
    private val packageManager: PackageManager = appContext.packageManager

    private val _snackbarMessage = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val snackbarMessage = _snackbarMessage.asSharedFlow()

    // Navigation events
    private val _navigationEvent = MutableSharedFlow<NavigationEvent>(extraBufferCapacity = 1)
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val _info = MutableStateFlow<Info?>(null)
    private val _loading = MutableStateFlow(true)

    val info: StateFlow<Info?> = _info
    val loading: StateFlow<Boolean> = _loading
    val isEmpty: StateFlow<Boolean> = _info.mapStateFlow { it == null }
    val dataId: StateFlow<String?> = _info.mapStateFlow { it?.dataId }
    val label: StateFlow<String> = _info.mapStateFlow { it?.title ?: "" }
    val desc: StateFlow<String> = _info.mapStateFlow { it?.remark ?: "" }
    val tags: StateFlow<List<String>> = _info.mapStateFlow { it?.tagList ?: emptyList() }
    val bgUrl: StateFlow<String?> = _info.mapStateFlow { it?.infoImgHUrl }
    val controlTypes: StateFlow<List<String>> = _info.mapStateFlow { it?.control ?: emptyList() }
    val relativeResources: StateFlow<List<Info>> = _info.mapStateFlow { it?.dataList ?: emptyList() }

    val gameType: StateFlow<GameDetailsType> = _info.mapStateFlow {
        when (it?.packetType) {
            "apk" -> GameDetailsType.APK
            "h5" -> GameDetailsType.H5
            else -> GameDetailsType.UNKNOW
        }
    }

    val packageName: StateFlow<String?> = combineStateFlows(gameType, _info) { gameType, info ->
        when (gameType) {
            GameDetailsType.APK -> info?.packageName
            GameDetailsType.H5 -> null
            GameDetailsType.UNKNOW -> null
        }
    }

    val latestVersionCode: StateFlow<Int?> = combineStateFlows(gameType, _info) { gameType, info ->
        when (gameType) {
            GameDetailsType.APK -> info?.versionCode
            GameDetailsType.H5 -> null
            GameDetailsType.UNKNOW -> null
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val downloadItem = dataId.filterNotNull().flatMapLatest {
        downloadManager.dlListDb.getByDataId(it)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val downloadItemState: StateFlow<IDownloadItemState?> =
        combineStateFlows(
            downloadMonitor.downloadListFlow,
            dataId,
        ) { downloadList, dataId ->
            downloadList.firstOrNull { it.dataId == dataId }
        }

    val percent: StateFlow<Int?> = combineStateFlows(downloadItemState, gameType) { item, gameType ->
        when (gameType) {
            GameDetailsType.APK -> if (item is ProcessingDownloadItemState) item.percent else null
            GameDetailsType.H5 -> null
            GameDetailsType.UNKNOW -> null
        }
    }

    val isPaused: StateFlow<Boolean> = combineStateFlows(downloadItem, gameType) { item, gameType ->
        when (gameType) {
            GameDetailsType.APK -> item?.status == DownloadStatus.Paused
            GameDetailsType.H5 -> false
            GameDetailsType.UNKNOW -> false
        }
    }

    val isDownloading: StateFlow<Boolean> = combineStateFlows(downloadItem, gameType) { item, gameType ->
        when (gameType) {
            GameDetailsType.APK -> item?.status == DownloadStatus.Downloading
            GameDetailsType.H5 -> false
            GameDetailsType.UNKNOW -> false
        }
    }

    val isCompleted: StateFlow<Boolean> = combineStateFlows(downloadItem, gameType) { item, gameType ->
        when (gameType) {
            GameDetailsType.APK -> item?.status == DownloadStatus.Completed
            GameDetailsType.H5 -> false
            GameDetailsType.UNKNOW -> false
        }
    }

    val isDownloadError: StateFlow<Boolean> = combineStateFlows(downloadItem, gameType) { item, gameType ->
        when (gameType) {
            GameDetailsType.APK -> item?.status == DownloadStatus.Error
            GameDetailsType.H5 -> false
            GameDetailsType.UNKNOW -> false
        }
    }

    val installState: StateFlow<InstallItemState?> =
        combineStateFlows(downloadItem, installManager.installListFlow) { item, installList ->
            item?.let { installList.firstOrNull { it.foreignKey == item.id } }
        }

    val isInstalling: StateFlow<Boolean> = installState.mapStateFlow { it?.isInstalling() ?: false }
    val isInstallError: StateFlow<Boolean> = installState.mapStateFlow { it?.isSuccess() == false }

    val packageInfo: StateFlow<PackageInfo?> =
        packageName.mapNotNull { it }.distinctUntilChanged().combine(installState) { packageName, _ ->
            packageManager.runCatching { getPackageInfo(packageName, 0) }.getOrNull()
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val isInstalled: StateFlow<Boolean> = packageInfo.mapStateFlow { it != null }

    val hasUpdate: StateFlow<Boolean> =
        combineStateFlows(isInstalled, latestVersionCode, packageInfo) { isInstalled, latestVersionCode, packageInfo ->
            isInstalled && latestVersionCode != null && latestVersionCode > (packageInfo?.longVersionCode ?: 0L)
        }

    val isGooglePlayInstalled: StateFlow<Boolean> = installManager.installEvents.map { _ ->
        packageManager.runCatching { getPackageInfo(GOOGLE_PLAY_PACKAGE_NAME, 0) }.getOrNull() != null
    }.distinctUntilChanged().stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        packageManager.runCatching { getPackageInfo(GOOGLE_PLAY_PACKAGE_NAME, 0) }.getOrNull() != null
    )

    val isNeedGooglePlay: StateFlow<Boolean> = _info.mapStateFlow { it?.requireGoogleplay == true }

    private val _showGooglePlayDialog = MutableStateFlow(false)

    val showGooglePlayDialog: StateFlow<Boolean> =
        combineStateFlows(_showGooglePlayDialog, isGooglePlayInstalled) { show, isInstalled ->
            show && !isInstalled
        }

    fun fetchInfo() {
        viewModelScope.launch {
            gameRepository.getInfo(param).catch {
                Log.e(TAG, "getInfo error: ${it.message}", it)
                _loading.value = false
                it.localizedMessage?.let { _snackbarMessage.tryEmit(it) }
            }.collect { info ->
                _info.value = info
                _loading.value = false
            }
        }
    }

    fun download(context: Context) {
        viewModelScope.launch {
            val value = _info.value
            if (value == null
                || value.packageName.isNullOrBlank()
                || value.versionCode == null
                || value.imgUrl.isBlank()
                || value.title.isBlank()
            ) {
                _snackbarMessage.tryEmit(context.getString(R.string.game_resource_error))
                Log.e(TAG, "download error: $value")
                return@launch
            }
            if (isNeedGooglePlay.value && !isGooglePlayInstalled.value) {
                _showGooglePlayDialog.value = true
                Log.w(TAG, "download error: google play is not installed")
                return@launch
            }
            _snackbarMessage.tryEmit(context.getString(R.string.downloading_tip))
            gameRepository.getDownloadUrl(param).catch {
                Log.e(TAG, "getDownloadUrl error: ${it.message}", it)
                it.localizedMessage?.let { _snackbarMessage.tryEmit(it) }
            }.collect { url ->
                val id = downloadManager.addDownload(
                    DownloadItem(
                        id = 0,
                        link = url,
                        name = "${value.packageName}_${value.versionCode}.apk",
                        folder = folder,
                        label = value.title,
                        imgUrl = value.imgUrl,
                        configId = value.configId,
                        dataId = value.dataId,
                        contentType = value.contentType,
                        dataType = value.dataType,
                        versionCode = value.versionCode,
                        packageName = value.packageName,
                    ), OnDuplicateStrategy.OverrideDownload
                )
                downloadManager.resume(id)
            }
        }
    }

    fun installApk(context: Context) {
        viewModelScope.launch {
            val value = downloadItem.value
            if (value == null) {
                Log.e(TAG, "installApk error: $value")
                _snackbarMessage.tryEmit(context.getString(R.string.game_resource_error))
                return@launch
            }
            installManager.install(value)
        }
    }

    fun pauseDownload() {
        viewModelScope.launch {
            downloadItem.value?.let { downloadManager.pause(it.id) }
        }
    }

    fun resumeDownload() {
        viewModelScope.launch {
            downloadItem.value?.let { downloadItem ->
                gameRepository.getDownloadUrl(downloadItem).catch {
                    Log.e(TAG, "resumeDownload: getDownloadUrl error", it)
                }.firstOrNull()?.let { url ->
                    downloadManager.updateDownloadItem(downloadItem.id) {
                        it.link = url
                    }
                    downloadManager.resume(downloadItem.id)
                }
            }
        }
    }

    fun cancelDownload() {
        viewModelScope.launch {
            downloadItem.value?.let {
                downloadManager.deleteDownload(it.id, { true })
            }
        }
    }

    fun playH5(context: Context) {
        val info = _info.value
        viewModelScope.launch {
            if (info?.skipPar.isNullOrBlank()) {
                Log.e(TAG, "playH5 error: skipPar is blank")
                _snackbarMessage.tryEmit(context.getString(R.string.game_resource_error))
                return@launch
            }
            Log.i(TAG, "playH5: ${info.skipPar}")
            _navigationEvent.tryEmit(NavigationEvent.NavigateToWeb(info.skipPar))
            playRecordDao.insertRecordWithLimit(
                record = PlayRecord(
                    dataId = info.dataId,
                    configId = info.configId,
                    contentType = info.contentType,
                    dataType = info.dataType,
                    title = info.title,
                    imgUrl = info.imgUrl,
                    lastPlayTime = System.currentTimeMillis(),
                ),
                10,
            )
            exportEvent(info.title)
        }
    }

    fun openApp(context: Context) {
        val info = _info.value
        viewModelScope.launch {
            if (info?.packageName.isNullOrBlank()) {
                Log.e(TAG, "openApp error: packageName is blank")
                _snackbarMessage.tryEmit(context.getString(R.string.game_resource_error))
                return@launch
            } else if (info.skipPar.isNullOrBlank().not()) {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    `package` = info.packageName
                    data = info.skipPar.toUri()
                }
                context.runCatching { startActivity(intent) }.onFailure {
                    Log.e(TAG, "openApp error: ${it.message}", it)
                    val launchIntent = context.packageManager.getLaunchIntentForPackage(info.packageName)
                    if (launchIntent != null) {
                        context.runCatching { startActivity(launchIntent) }.onFailure {
                            _snackbarMessage.tryEmit(context.getString(R.string.game_resource_error))
                            Log.e(TAG, "openApp error: ${it.message}", it)
                        }
                    }
                }
            } else {
                val intent = context.packageManager.getLaunchIntentForPackage(info.packageName)
                if (intent != null) {
                    context.runCatching { startActivity(intent) }.onFailure {
                        _snackbarMessage.tryEmit(context.getString(R.string.game_resource_error))
                        Log.e(TAG, "openApp error: ${it.message}", it)
                    }
                } else {
                    Log.e(TAG, "openApp error: ${info.packageName} not found")
                }
            }
            playRecordDao.insertRecordWithLimit(
                record = PlayRecord(
                    dataId = info.dataId,
                    configId = info.configId,
                    contentType = info.contentType,
                    dataType = info.dataType,
                    title = info.title,
                    imgUrl = info.imgUrl,
                    lastPlayTime = System.currentTimeMillis(),
                ),
                10,
            )
            exportEvent(info.title)
        }
    }

    fun dismissGooglePlayDialog() {
        _showGooglePlayDialog.value = false
    }

    fun downloadGooglePlayApp(context: Context) {
        viewModelScope.launch {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = "appmarket://details?packageName=$GOOGLE_PLAY_PACKAGE_NAME".toUri()
                `package` = "com.kgzn.appstore"
            }
            context.runCatching {
                startActivity(intent)
            }.onFailure {
                Log.e(TAG, "downloadGooglePlayApp error: ${it.message}", it)
            }
        }
    }

    fun exportEvent(gameName: String) {
        EventSDK.getInstance().onEvent(
            "Click_gamecenter_Games_Start", mapOf(
                "attr_sn" to "gameName",
                "attr_value" to gameName,
            )
        )
    }
}

sealed interface NavigationEvent {
    data class NavigateToWeb(val url: String) : NavigationEvent
    data class NavigateToGameDetails(val route: GameDetailsRoute) : NavigationEvent
}

enum class GameDetailsType {
    UNKNOW,
    APK,
    H5,
}
