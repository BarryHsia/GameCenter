package com.kgzn.gamecenter.data.repository

import android.util.Log
import com.kgzn.gamecenter.data.AppApi
import com.kgzn.gamecenter.data.ContentConfig
import com.kgzn.gamecenter.data.Info
import com.kgzn.gamecenter.data.InfoParam
import com.kgzn.gamecenter.data.Search2
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameRepository @Inject constructor(
    private val appApi: AppApi,
) {
    companion object {
        private const val TAG = "GameRepository"
    }

    fun getAllContentConfigs(): Flow<List<ContentConfig>> = appApi.getAllContentConfigs()

    fun getInfo(param: InfoParam): Flow<Info?> = appApi.getInfo(param)

    fun search(key: String): Flow<List<Search2>> = appApi.search2(key)

    fun getDownloadUrl(param: InfoParam): Flow<String> = appApi.getDownloadUrl(param)
}
