package com.kgzn.gamecenter.feature.network.di

import com.kgzn.gamecenter.feature.network.ConnectivityManagerNetworkMonitor
import com.kgzn.gamecenter.feature.network.NetworkMonitor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {

    @Binds
    internal abstract fun bindsNetworkMonitor(
        networkMonitor: ConnectivityManagerNetworkMonitor,
    ): NetworkMonitor
}