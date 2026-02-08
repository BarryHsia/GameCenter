package com.kgzn.gamecenter.data.local

import com.kgzn.gamecenter.data.AppApi
import com.kgzn.gamecenter.data.ContentConfig
import com.kgzn.gamecenter.data.Info
import com.kgzn.gamecenter.data.InfoParam
import com.kgzn.gamecenter.data.Search2
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LocalAppApiImpl : AppApi {
    override fun getAllContentConfigs(): Flow<List<ContentConfig>> = flow {
        emit(LocalDataProvider.allContentConfigs)
    }

    override fun getInfo(param: InfoParam): Flow<Info> = flow {
        emit(LocalDataProvider.info)
    }

    override fun search2(key: String): Flow<List<Search2>> = flow {
        LocalDataProvider.search2s.filter {
            it.title.contains(key) || it.remark.contains(key)
        }.let {
            emit(it)
        }
    }

    override fun getDownloadUrl(param: InfoParam): Flow<String> = flow {
        emit(LocalDataProvider.downloadUrl)
    }
}