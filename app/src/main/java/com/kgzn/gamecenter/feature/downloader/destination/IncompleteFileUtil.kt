package com.kgzn.gamecenter.feature.downloader.destination

import java.io.File

object IncompleteFileUtil {
    private const val SYSTEM_MAXIMUM_FILE_LENGTH = 255
    private const val SYSTEM_MAXIMUM_FULL_PATH_LENGTH = 259
    private fun createExtension(id: Long): String {
        return ".dl-$id.abdm.part"
    }

    fun addIncompleteIndicator(file: File, id: Long): File {
        val ext = createExtension(id)
        if (!file.name.endsWith(ext)) {
            val trimmedFileName = file.name.take(SYSTEM_MAXIMUM_FILE_LENGTH - ext.length)
            return file.parentFile.resolve(trimmedFileName + ext)
        }
        return file
    }
}
