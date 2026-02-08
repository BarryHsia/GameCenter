package com.kgzn.gamecenter

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.SnackbarHostState
import com.kgzn.gamecenter.db.playrecord.PlayRecordDao
import com.kgzn.gamecenter.designsystem.theme.GcTheme
import com.kgzn.gamecenter.feature.downloader.DownloadManager
import com.kgzn.gamecenter.feature.downloader.DownloadManagerEvents
import com.kgzn.gamecenter.feature.downloader.exception.PrepareDestinationFailedException
import com.kgzn.gamecenter.feature.installer.InstallEvents
import com.kgzn.gamecenter.feature.installer.InstallManager
import com.kgzn.gamecenter.feature.network.NetworkMonitor
import com.kgzn.gamecenter.ui.GcApp
import com.kgzn.gamecenter.ui.gamedetails.getGameDetailsRoute
import com.kgzn.gamecenter.ui.gamedetails.isGameDetailsUri
import com.kgzn.gamecenter.ui.home.HomeRoute
import com.kgzn.gamecenter.ui.rememberGcAppState
import com.kgzn.gamecenter.ui.web.WebRoute
import com.kgzn.gamecenter.ui.web.isWebUri
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"
    }

    private val scope by lazy { CoroutineScope(SupervisorJob()) }

    @Inject
    lateinit var downloadManager: DownloadManager

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    @Inject
    lateinit var playRecordDao: PlayRecordDao

    @Inject
    lateinit var installManager: InstallManager

    private val snackbarHostState = SnackbarHostState()

    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: $intent")
        startObserve()
        setContent {
            var startDestination: Any = HomeRoute
            val uri = intent.data
            if (uri != null) {
                if (uri.isGameDetailsUri()) {
                    val gameDetailsRoute = uri.getGameDetailsRoute()
                    if (gameDetailsRoute != null) {
                        startDestination = gameDetailsRoute
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar(getString(R.string.invalid_parameter))
                        }
                    }
                } else if (uri.isWebUri()) {
                    startDestination = WebRoute(uri.toString())
                }
            }

            GcTheme {
                GcApp(
                    appState = rememberGcAppState(
                        networkMonitor = networkMonitor,
                        playRecordDao = playRecordDao,
                        startDestination = startDestination,
                        snackbarHostState = snackbarHostState,
                    )
                )
            }
        }
    }

    /**
     * for some stupid guys, they fuck modify the KEYCODE_DPAD_CENTER key
     * to implements some disgusting feature instead of using standard implement.
     **/
    private var cacheEvents: KeyEvent? = null
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_DPAD_CENTER
            && event.action == KeyEvent.ACTION_UP
            && event.isCanceled
            && event.repeatCount == 0
            && cacheEvents != event
        ) {
            cacheEvents = event.copy()
            return true
        }
        return super.dispatchKeyEvent(event)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (cacheEvents != null && !hasFocus) {
            dispatchKeyEvent(cacheEvents!!)
        }
    }

    fun startObserve() {
        Log.d(TAG, "startObserve: ")
        downloadManager.listOfJobsEvents.filter {
            it is DownloadManagerEvents.OnJobCanceled && it.e is PrepareDestinationFailedException
        }.onEach {
            downloadManager.deleteDownload(it.downloadItem.id, { true })
            scope.launch {
                snackbarHostState.showSnackbar(getString(R.string.insufficient_storage))
            }
        }.launchIn(scope)

        networkMonitor.isOnline.distinctUntilChanged().filter { !it }.onEach {
            scope.launch {
                snackbarHostState.showSnackbar(getString(R.string.no_network))
            }
        }.launchIn(scope)

        installManager.installEvents.onEach {
            when (it) {
                is InstallEvents.OnPackageInstalling -> {
                    scope.launch { snackbarHostState.showSnackbar(getString(R.string.installing_tip, it.label)) }
                }

                is InstallEvents.OnPackageInstalled -> {
                    if (it.isSuccess()) {
                        scope.launch { snackbarHostState.showSnackbar(getString(R.string.install_success)) }
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                getString(
                                    R.string.install_failed,
                                    it.statusMsg
                                )
                            )
                        }
                    }
                }

                is InstallEvents.OnPackageUninstalling -> {
                    scope.launch { snackbarHostState.showSnackbar(getString(R.string.uninstalling_tip, it.label)) }
                }

                is InstallEvents.OnPackageUninstalled -> {
                    if (it.isSuccess()) {
                        scope.launch { snackbarHostState.showSnackbar(getString(R.string.uninstall_success)) }
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                getString(
                                    R.string.uninstall_failed,
                                    it.statusMsg
                                )
                            )
                        }
                    }
                }
            }
        }.launchIn(scope)
    }
}
