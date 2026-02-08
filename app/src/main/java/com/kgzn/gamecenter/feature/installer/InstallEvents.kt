package com.kgzn.gamecenter.feature.installer

import android.content.pm.PackageInstaller

sealed interface InstallEvents {

    interface Result {
        val status: Int
        val statusMsg: String?

        fun isSuccess(): Boolean = status == PackageInstaller.STATUS_SUCCESS
    }

    data class OnPackageInstalling(
        val label: String = "",
    ) : InstallEvents

    data class OnPackageInstalled(
        override val status: Int,
        override val statusMsg: String?,
        val packageName: String?
    ) : InstallEvents, Result

    data class OnPackageUninstalling(
        val label: String = "",
    ) : InstallEvents

    data class OnPackageUninstalled(
        override val status: Int,
        override val statusMsg: String?,
        val packageName: String?
    ) : InstallEvents, Result
}