package com.kgzn.gamecenter.feature.installer

import android.content.pm.PackageInstaller

data class InstallItemState(
    val path: String,
    val status: Int? = null,
    val msg: String? = null,
    val sessionId: Int? = null,
    val foreignKey: Long = 0,
) {
    fun isInstalling() = status == null

    fun isFinished() = status != null

    fun isSuccess() = status == PackageInstaller.STATUS_SUCCESS
}
