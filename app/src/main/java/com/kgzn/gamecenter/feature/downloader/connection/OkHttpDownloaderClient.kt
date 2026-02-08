package com.kgzn.gamecenter.feature.downloader.connection

import com.kgzn.gamecenter.feature.downloader.connection.response.ResponseInfo
import com.kgzn.gamecenter.feature.downloader.downloaditem.IDownloadCredentials
import com.kgzn.gamecenter.feature.downloader.utils.await
import okhttp3.Call
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class OkHttpDownloaderClient(
    private val okHttpClient: OkHttpClient,
    private val defaultUserAgentProvider: UserAgentProvider,
) : DownloaderClient() {
    private fun newCall(
        downloadCredentials: IDownloadCredentials,
        start: Long?,
        end: Long?,
        extraBuilder: Request.Builder.() -> Unit,
    ): Call {
        val rangeHeader = start?.let {
            createRangeHeader(start, end)
        }
        return okHttpClient
            .newCall(
                Request.Builder()
                    .url(downloadCredentials.link)
                    .apply {
                        defaultHeadersInFirst().forEach { (k, v) ->
                            header(k, v)
                        }
                        // we don't to add something that we sure that it will be overridden later
                        if (downloadCredentials.userAgent == null) {
                            // only add default user agent if we don't specify it
                            defaultUserAgentProvider.getUserAgent()?.let { userAgent ->
                                header("User-Agent", userAgent)
                            }
                        }
                        downloadCredentials.headers
                            ?.filter {
                                //OkHttp handles this header and if we override it,
                                //makes redirected links to have this "Host" instead of their own!, and cause error
                                !it.key.equals("Host", true)
                            }
                            ?.forEach { (k, v) ->
                                header(k, v)
                            }
                        defaultHeadersInLast().forEach { (k, v) ->
                            header(k, v)
                        }
                        val username = downloadCredentials.username
                        val password = downloadCredentials.password
                        if (username?.isNotBlank() == true && password?.isNotBlank() == true) {
                            header("Authorization", Credentials.basic(username, password))
                        }
                        downloadCredentials.userAgent?.let { userAgent ->
                            header("User-Agent", userAgent)
                        }
                    }
                    .apply(extraBuilder)
                    .apply {
                        if (rangeHeader != null) {
                            header(rangeHeader.first, rangeHeader.second)
                        }
                    }
                    .build()
            )
    }


    override suspend fun head(
        credentials: IDownloadCredentials,
        start: Long?,
        end: Long?,
    ): ResponseInfo {
        newCall(
            downloadCredentials = credentials,
            start = start,
            end = end,
            extraBuilder = {
//                head()
            }
        ).await().use { response ->
//            println(response.headers)
            return createFileInfo(response)
        }
    }

    private fun createFileInfo(response: Response): ResponseInfo {
        return ResponseInfo(
            statusCode = response.code,
            message = response.message,
            requestUrl = response.request.url.toString(),
            requestHeaders = response.request.headers.associate { (key, value) ->
                key.lowercase() to value
            },
            responseHeaders = response.headers.associate { (key, value) ->
                key.lowercase() to value
            },
        )
    }

    override suspend fun connect(
        credentials: IDownloadCredentials,
        start: Long?,
        end: Long?,
    ): Connection {
        val response = newCall(
            downloadCredentials = credentials,
            start = start,
            end = end,
            extraBuilder = {
                get()
            }
        ).await()
        val body = runCatching {
            requireNotNull(response.body) {
                "body is null"
            }
        }.onFailure {
            response.close()
        }.getOrThrow()
        return Connection(
            source = body.source(),
            contentLength = body.contentLength(),
            closeable = response,
            responseInfo = createFileInfo(response)
        )
    }
}
