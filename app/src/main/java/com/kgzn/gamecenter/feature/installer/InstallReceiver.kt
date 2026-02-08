package com.kgzn.gamecenter.feature.installer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@AndroidEntryPoint
class InstallReceiver : BroadcastReceiver() {

    companion object {
        const val TAG = "InstallReceiver"
    }

    @Inject
    lateinit var installManager: InstallManager

    override fun onReceive(context: Context?, intent: Intent) {
        Log.i(TAG, "onReceive: $intent")
        if (intent.action == Intent.ACTION_INSTALL_PACKAGE) {
            val sessionId = intent.getIntExtra(PackageInstaller.EXTRA_SESSION_ID, -1)
            val status = intent.getIntExtra(PackageInstaller.EXTRA_STATUS, PackageInstaller.STATUS_FAILURE)
            val msg = intent.getStringExtra(PackageInstaller.EXTRA_STATUS_MESSAGE)
            val packageName = intent.getStringExtra(PackageInstaller.EXTRA_PACKAGE_NAME)
            Log.i(TAG, "onReceive: {packageName=$packageName, sessionId=$sessionId, status=$status, msg=$msg")

            installManager.installListFlow.update {
                it.map { if (it.sessionId == sessionId) it.copy(status = status, msg = msg) else it }
            }
            installManager.installEvents.tryEmit(
                InstallEvents.OnPackageInstalled(
                    status = status,
                    statusMsg = msg,
                    packageName = packageName,
                )
            )
        } else if (intent.action == Intent.ACTION_UNINSTALL_PACKAGE) {
            val status = intent.getIntExtra(PackageInstaller.EXTRA_STATUS, PackageInstaller.STATUS_FAILURE)
            val packageName = intent.getStringExtra(PackageInstaller.EXTRA_PACKAGE_NAME)
            val msg = intent.getStringExtra(PackageInstaller.EXTRA_STATUS_MESSAGE)
            Log.i(TAG, "onReceive: {packageName=$packageName, status=$status, msg=$msg")

            if (packageName == null) return
            installManager.uninstallListFlow.update {
                it.map { if (it.packageName == packageName) it.copy(status = status, msg = msg) else it }
            }
            installManager.installEvents.tryEmit(
                InstallEvents.OnPackageUninstalled(
                    status = status,
                    statusMsg = msg,
                    packageName = packageName,
                )
            )
        } else if (intent.action == Intent.ACTION_PACKAGE_ADDED) {
            val packageName = intent.data?.schemeSpecificPart
            Log.i(TAG, "onReceive: {packageName=$packageName}")
            if (packageName == null || packageName != "com.android.vending") return
            installManager.installEvents.tryEmit(
                InstallEvents.OnPackageInstalled(
                    status = PackageInstaller.STATUS_SUCCESS,
                    statusMsg = "success",
                    packageName = packageName,
                )
            )

        } else if (intent.action == Intent.ACTION_PACKAGE_REMOVED) {
            val packageName = intent.data?.schemeSpecificPart
            Log.i(TAG, "onReceive: {packageName=$packageName}")
            if (packageName == null || packageName != "com.android.vending") return
            installManager.installEvents.tryEmit(
                InstallEvents.OnPackageUninstalled(
                    status = PackageInstaller.STATUS_SUCCESS,
                    statusMsg = "success",
                    packageName = packageName,
                )
            )
        }
    }
}