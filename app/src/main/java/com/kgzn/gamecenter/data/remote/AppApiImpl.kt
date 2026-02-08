package com.kgzn.gamecenter.data.remote

import android.content.Context
import android.util.Log
import com.google.gson.GsonBuilder
import com.kgzn.gamecenter.data.AppApi
import com.kgzn.gamecenter.data.ContentConfig
import com.kgzn.gamecenter.data.Info
import com.kgzn.gamecenter.data.InfoParam
import com.kgzn.gamecenter.data.Search2
import com.kgzn.gamecenter.data.remote.interceptor.RetryInterceptor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppApiImpl(private val context: Context) : AppApi {

    companion object {
        private const val TAG = "ContentConfigsRepositoryImpl"
        const val BASE_URL = "http://appstore.intelligen.ltd:8084"
        const val CACHE_SIZE = 1024 * 1024 * 10L
    }

    private val apiService: ApiService by lazy {
        val cacheFile = context.cacheDir.resolve("retrofit").apply {
            mkdirs()
        }
        val cache = Cache(cacheFile, CACHE_SIZE)
        val client = OkHttpClient.Builder()
            .addInterceptor(RetryInterceptor(3, 0))
            .cache(cache)
            .build()
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(ApiService::class.java)
    }

    override fun getAllContentConfigs(): Flow<List<ContentConfig>> = flow {
        val response = apiService.getContentConfig(
            GetContentConfigRequest().also { Log.d(TAG, "getAllContentConfigs: $it") }
        )
        Log.d(TAG, "getAllContentConfigs: { code: ${response.code}, msg: ${response.msg} }")
        if (response.code == 0) {
            emit(response.data.orEmpty().map { contentConfig ->
                contentConfig.copy(componentList = contentConfig.componentList.filter { component ->
                    val resourceIsEmpty = component.resourceList.isEmpty()
                    if (resourceIsEmpty) {
                        Log.d(TAG, "getAllContentConfigs: ${contentConfig.name} -> ${component.name} resource is empty")
                    }
                    !resourceIsEmpty
                })
            })
        } else {
            throw ApiException(response.code, response.msg)
        }
    }

    override fun getInfo(param: InfoParam): Flow<Info?> = flow {
        val response = apiService.getInfo(
            GetInfoRequest(
                configId = param.configId,
                dataId = param.dataId,
                contentType = param.contentType,
                dataType = param.dataType,
            ).also {
                Log.d(TAG, "getInfo: $it")
            }
        )
        Log.d(TAG, "getInfo: { code: ${response.code}, msg: ${response.msg} }")
        if (response.code == 0) {
            emit(response.info?.copy(dataList = response.dataList.orEmpty()))
        } else {
            throw ApiException(response.code, response.msg)
        }
    }

    override fun search2(key: String): Flow<List<Search2>> = flow {
        val response = apiService.search2(
            Search2Parameter(key = key).also {
                Log.d(TAG, "search2: $it")
            }
        )
        Log.d(TAG, "search2: { code: ${response.code}, msg: ${response.msg} }")
        if (response.code == 0) {
            emit(response.data.orEmpty())
        } else {
            throw ApiException(response.code, response.msg)
        }
    }

    override fun getDownloadUrl(param: InfoParam): Flow<String> = flow {
        val response = apiService.getDownloadUrl(
            GetDownloadUrlRequest(
                dataId = param.dataId,
                contentType = param.contentType,
                dataType = param.dataType,
            ).also {
                Log.d(TAG, "getDownloadUrl: $it")
            }
        )
        Log.d(TAG, "getDownloadUrl: $response")
        if (response.code == 0) {
            emit(response.data.orEmpty())
        } else {
            throw ApiException(response.code, response.msg)
        }
    }
}