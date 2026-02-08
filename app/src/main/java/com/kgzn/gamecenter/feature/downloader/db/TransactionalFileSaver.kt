package com.kgzn.gamecenter.feature.downloader.db


import com.kgzn.gamecenter.feature.downloader.utils.tryAtomicMove
import kotlinx.serialization.json.Json
import okio.FileSystem
import okio.Path.Companion.toOkioPath
import java.io.File

class TransactionalFileSaver(
    val json: Json,
) {
    fun getBakFile(file: File) = File("$file.tmp")
    inline fun <reified T> writeObject(file: File, t: T) {
        val bakFile = getBakFile(file)
        val text = json.encodeToString(t)
        runCatching {
            FileSystem.SYSTEM.write(
                file = bakFile.toOkioPath()
            ) {
                writeUtf8(text)
            }
        }.onSuccess {
            bakFile.tryAtomicMove(file)
        }.getOrThrow()
    }

    inline fun <reified T> readObject(file: File): T? {
        return runCatching {
            val text = FileSystem.SYSTEM.read(file.toOkioPath()) {
                readUtf8()
            }
            json.decodeFromString<T>(text)
        }.getOrNull()
    }
}
