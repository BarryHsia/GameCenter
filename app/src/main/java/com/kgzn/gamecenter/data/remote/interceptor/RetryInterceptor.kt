package com.kgzn.gamecenter.data.remote.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class RetryInterceptor(
    private val maxRetry: Int,
    private val retryDelay: Long,
) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var response: Response? = null
        var lastException: IOException? = null

        repeat(maxRetry) { attempt ->
            try {
                response?.close()
                response = chain.proceed(request)
                if (response!!.isSuccessful) {
                    return response!!
                }
            } catch (e: IOException) {
                lastException = e
                if (attempt >= maxRetry - 1) throw e
            }
            try {
                Thread.sleep(retryDelay)
            } catch (_: InterruptedException) {
                Thread.currentThread().interrupt()
            }
        }

        return response ?: throw (lastException ?: IOException("Retry exhausted"))
    }
}
