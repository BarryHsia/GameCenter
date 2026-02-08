package com.kgzn.gamecenter.feature.downloader.utils.osfileutil

import java.io.File

/**
 * it uses the jvm default.
 */
internal class JVMFileUtils : FileUtilsBase() {
    override fun openFileInternal(file: File): Boolean {
        throw NotImplementedError("JVMFileUtils.openFileInternal")
    }

    override fun openFolderOfFileInternal(file: File): Boolean {
        throw NotImplementedError("JVMFileUtils.openFolderOfFileInternal")
    }

    override fun openFolderInternal(folder: File): Boolean {
        throw NotImplementedError("JVMFileUtils.openFolderInternal")
    }
}