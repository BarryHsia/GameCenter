package com.kgzn.gamecenter.feature.installer

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageInstaller
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.kgzn.gamecenter.feature.downloader.downloaditem.DownloadItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream

class InstallManager(
    private val context: Context,
) {

    companion object {
        const val TAG = "InstallManager"
    }

    private val scope = CoroutineScope(SupervisorJob())

    val installListFlow: MutableStateFlow<List<InstallItemState>> = MutableStateFlow(emptyList())
    val uninstallListFlow: MutableStateFlow<List<UninstallState>> = MutableStateFlow(emptyList())
    val installEvents: MutableSharedFlow<InstallEvents> = MutableSharedFlow(extraBufferCapacity = 32)

    init {
        installListFlow
            .subscriptionCount
            .map { it > 0 }
            .distinctUntilChanged().onEach { isUsed ->
                if (!isUsed) {
                    clearWhenInstallFinished()
                }
            }.launchIn(scope)

        uninstallListFlow
            .subscriptionCount
            .map { it > 0 }
            .distinctUntilChanged().onEach { isUsed ->
                if (!isUsed) {
                    clearWhenUninstalledFinished()
                }
            }.launchIn(scope)

        ContextCompat.registerReceiver(
            context,
            InstallReceiver(),
            IntentFilter().apply {
                addAction(Intent.ACTION_PACKAGE_ADDED)
                addAction(Intent.ACTION_PACKAGE_REMOVED)
                addDataScheme("package")
            },
            ContextCompat.RECEIVER_EXPORTED
        )
    }

    fun clearWhenInstallFinished() {
        Log.d(TAG, "clearWhenInstallFinished")
        installListFlow.update { it.filterNot { it.isFinished() } }
    }

    fun clearWhenUninstalledFinished() {
        Log.d(TAG, "clearWhenUninstalledFinished")
        uninstallListFlow.update { it.filterNot { it.status != null } }
    }

    fun install(downloadItem: DownloadItem) {
        Log.d(TAG, "install: $downloadItem")
        scope.launch {
            if (installListFlow.value.any { it.foreignKey == downloadItem.id && it.isFinished().not() }) {
                Log.d(TAG, "install: $downloadItem is installing")
                return@launch
            }
            val path = File(downloadItem.folder, downloadItem.name).absolutePath
            installListFlow.update {
                it.filterNot { it.foreignKey == downloadItem.id } + InstallItemState(
                    path = path,
                    foreignKey = downloadItem.id
                )
            }
            installEvents.tryEmit(InstallEvents.OnPackageInstalling(downloadItem.label ?: ""))

            runCatching {
                val installer = context.packageManager.packageInstaller
                val params = PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    params.setPackageSource(PackageInstaller.PACKAGE_SOURCE_STORE)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    params.setInstallerPackageName(context.packageName)
                }

                val sessionId = installer.createSession(params)
                installer.openSession(sessionId).use { session ->
                    session.openWrite("apk", 0, -1).use { output ->
                        FileInputStream(path).use { input ->
                            input.copyTo(output)
                        }
                        session.fsync(output)
                    }
                    val intent = Intent(context, InstallReceiver::class.java)
                    intent.setAction(Intent.ACTION_INSTALL_PACKAGE)
                    val pendingIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_MUTABLE)
                    session.commit(pendingIntent.intentSender)

                    installListFlow.update { it.map { item -> if (item.path == path) item.copy(sessionId = sessionId) else item } }
                }
            }.onFailure { e ->
                installListFlow.update {
                    it.map { item ->
                        if (item.path == path) item.copy(
                            status = PackageInstaller.STATUS_FAILURE,
                            msg = e.message
                        ) else item
                    }
                }
                Log.e(TAG, "install: $path failed", e)
            }
        }
    }

    fun uninstall(packageName: String) {
        scope.launch {
            Log.d(TAG, "uninstall: $packageName")
            if (uninstallListFlow.value.any { it.packageName == packageName && it.status == null }) {
                Log.d(TAG, "uninstall: $packageName is uninstalling")
                return@launch
            }
            uninstallListFlow.update { it.filterNot { it.packageName == packageName } + UninstallState(packageName) }
            runCatching {
                val pm = context.packageManager
                val label = pm.getApplicationLabel(pm.getApplicationInfo(packageName, 0)).toString()
                installEvents.tryEmit(InstallEvents.OnPackageUninstalling(label))
                val installer = pm.packageInstaller
                val intent = Intent(context, InstallReceiver::class.java)
                intent.setAction(Intent.ACTION_UNINSTALL_PACKAGE)
                val pendingIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_MUTABLE)
                installer.uninstall(packageName, pendingIntent.intentSender)
            }.onFailure { e ->
                uninstallListFlow.update {
                    it.map { item ->
                        if (item.packageName == packageName) item.copy(
                            status = PackageInstaller.STATUS_FAILURE,
                            msg = e.message
                        ) else item
                    }
                }
                Log.e(TAG, "uninstall: $packageName failed", e)
            }
        }
    }
}
