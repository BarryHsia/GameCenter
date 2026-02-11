package com.kgzn.gamecenter.utils

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Provides access to app resources and system services
 * Separates Context dependencies from ViewModels
 */
@Singleton
class AppResourceProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {
    val cacheDir: String
        get() = context.cacheDir.path

    val packageManager: PackageManager
        get() = context.packageManager

    fun getPackageInfo(packageName: String): PackageInfo? {
        return packageManager.runCatching {
            getPackageInfo(packageName, 0)
        }.getOrNull()
    }

    fun isPackageInstalled(packageName: String): Boolean {
        return getPackageInfo(packageName) != null
    }
}
