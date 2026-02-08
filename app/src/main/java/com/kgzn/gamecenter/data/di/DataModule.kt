package com.kgzn.gamecenter.data.di

import android.content.Context
import com.kgzn.gamecenter.data.AppApi
import com.kgzn.gamecenter.data.remote.AppApiImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideAppApi(@ApplicationContext context: Context): AppApi {
        return AppApiImpl(context)
    }
}