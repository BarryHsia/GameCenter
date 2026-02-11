package com.kgzn.gamecenter.utils

import android.util.Log
import com.kgzn.gamecenter.BuildConfig

/**
 * 安全日志工具类
 * - Debug 模式：记录所有日志
 * - Release 模式：只记录错误和警告，自动脱敏
 */
object Logger {
    
    private const val MAX_LOG_LENGTH = 4000
    
    fun d(tag: String, message: String) {
        if (BuildConfig.DEBUG) {
            logLongMessage(tag, message, Log.DEBUG)
        }
    }
    
    fun i(tag: String, message: String) {
        if (BuildConfig.DEBUG) {
            logLongMessage(tag, message, Log.INFO)
        }
    }
    
    fun w(tag: String, message: String, throwable: Throwable? = null) {
        val sanitized = sanitizeMessage(message)
        logLongMessage(tag, sanitized, Log.WARN, throwable)
    }
    
    fun e(tag: String, message: String, throwable: Throwable? = null) {
        val sanitized = sanitizeMessage(message)
        logLongMessage(tag, sanitized, Log.ERROR, throwable)
    }
    
    /**
     * 脱敏敏感信息
     */
    private fun sanitizeMessage(message: String): String {
        return message
            .replace(Regex("token=\\w+"), "token=***")
            .replace(Regex("password=\\w+"), "password=***")
            .replace(Regex("key=\\w+"), "key=***")
            .replace(Regex("secret=\\w+"), "secret=***")
            .replace(Regex("\\d{16}"), "****-****-****-****") // 信用卡号
    }
    
    /**
     * 处理长日志（超过 4000 字符会被截断）
     */
    private fun logLongMessage(tag: String, message: String, level: Int, throwable: Throwable? = null) {
        if (message.length <= MAX_LOG_LENGTH) {
            logMessage(tag, message, level, throwable)
            return
        }
        
        var index = 0
        while (index < message.length) {
            val end = minOf(index + MAX_LOG_LENGTH, message.length)
            val chunk = message.substring(index, end)
            logMessage(tag, chunk, level, if (index == 0) throwable else null)
            index = end
        }
    }
    
    private fun logMessage(tag: String, message: String, level: Int, throwable: Throwable? = null) {
        when (level) {
            Log.DEBUG -> Log.d(tag, message)
            Log.INFO -> Log.i(tag, message)
            Log.WARN -> if (throwable != null) Log.w(tag, message, throwable) else Log.w(tag, message)
            Log.ERROR -> if (throwable != null) Log.e(tag, message, throwable) else Log.e(tag, message)
        }
    }
}
