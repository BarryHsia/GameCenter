package com.kgzn.gamecenter.feature.installer.di

import android.content.Context
import com.kgzn.gamecenter.feature.installer.InstallManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object InstallModule {

    @Provides
    @Singleton
    fun provideInstallManager(@ApplicationContext context: Context): InstallManager {
        return InstallManager(context)
    }
}