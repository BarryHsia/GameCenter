package com.kgzn.gamecenter.feature.downloader.utils

import com.kgzn.gamecenter.feature.downloader.utils.osfileutil.FileUtils
import java.io.File

object PathValidator {
    fun canWriteToThisPath(path: String): Boolean {
        return FileUtils.canWriteInThisFolder(path)
    }

    fun isValidPath(path: String): Boolean {
        if (path.isEmpty()) return false
        return runCatching {
            File(path).canonicalFile
            true
        }.getOrElse { false }
    }
}
