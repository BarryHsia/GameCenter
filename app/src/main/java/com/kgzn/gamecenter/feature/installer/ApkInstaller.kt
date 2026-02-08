package com.kgzn.gamecenter.feature.installer

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller
import android.os.Build
import android.util.Log
import android.util.SparseArray
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.io.FileInputStream

object ApkInstaller {

    const val TAG = "ApkInstaller"

    private val installCallback: SparseArray<(Int) -> Unit> = SparseArray()
    private val uninstallCallback: MutableMap<String, (Int) -> Unit> = mutableMapOf()

    fun install(context: Context, filepath: String): Flow<Int> = callbackFlow {
        val installer = context.packageManager.packageInstaller
        val params = PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            params.setPackageSource(PackageInstaller.PACKAGE_SOURCE_STORE)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            params.setInstallerPackageName(context.packageName)
        }
        val sessionId = runCatching {
            val sessionId = installer.createSession(params)
            installer.openSession(sessionId).use { session ->
                session.openWrite("apk", 0, -1).use { output ->
                    FileInputStream(filepath).use { input ->
                        input.copyTo(output)
                    }
                    session.fsync(output)
                }
                val intent = Intent(context, ResultReceiver::class.java)
                intent.setAction(Intent.ACTION_INSTALL_PACKAGE)
                val pendingIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_MUTABLE)
                session.commit(pendingIntent.intentSender)
            }
            sessionId
        }.onFailure {
            Log.e(TAG, "install: ", it)
            trySend(PackageInstaller.STATUS_FAILURE)
        }.getOrNull() ?: -1

        if (sessionId != -1) {
            installCallback.put(sessionId, ::trySend)
        }
        awaitClose {
            installCallback.remove(sessionId)
        }
    }

    fun uninstall(context: Context, packageName: String): Flow<Int> = callbackFlow {
        runCatching {
            val installer = context.packageManager.packageInstaller
            val intent = Intent(context, ResultReceiver::class.java)
            intent.setAction(Intent.ACTION_UNINSTALL_PACKAGE)
            val pendingIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_MUTABLE)
            installer.uninstall(packageName, pendingIntent.intentSender)
        }.onFailure {
            trySend(PackageInstaller.STATUS_FAILURE)
        }
        uninstallCallback.put(packageName, ::trySend)
        awaitClose { uninstallCallback.remove(packageName) }
    }

    class ResultReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            if (intent.action == Intent.ACTION_INSTALL_PACKAGE) {
                val sessionId = intent.getIntExtra(PackageInstaller.EXTRA_SESSION_ID, -1)
                val status = intent.getIntExtra(PackageInstaller.EXTRA_STATUS, PackageInstaller.STATUS_FAILURE)
                Log.d(TAG, "onReceive: sessionId=$sessionId, status=$status")
                installCallback.get(sessionId)?.invoke(status)
            } else if (intent.action == Intent.ACTION_UNINSTALL_PACKAGE) {
                val status = intent.getIntExtra(PackageInstaller.EXTRA_STATUS, PackageInstaller.STATUS_FAILURE)
                Log.d(TAG, "onReceive: status=$status")

            }
        }
    }
}