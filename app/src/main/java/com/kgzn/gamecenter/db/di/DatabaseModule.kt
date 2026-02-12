package com.kgzn.gamecenter.db.di

import android.content.Context
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.kgzn.gamecenter.BuildConfig
import com.kgzn.gamecenter.db.AppDatabase
import com.kgzn.gamecenter.db.playrecord.PlayRecordDao
import com.kgzn.gamecenter.feature.downloader.db.IDownloadListDb
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.zetetic.database.sqlcipher.SQLiteDatabase
import net.zetetic.database.sqlcipher.SQLiteDatabaseHook
import net.zetetic.database.sqlcipher.SQLiteOpenHelper
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        // 使用 SECRET_KEY 作为数据库加密密钥
        val passphrase = BuildConfig.SECRET_KEY.toCharArray()
        val factory = object : SupportSQLiteOpenHelper.Factory {
            override fun create(config: SupportSQLiteOpenHelper.Configuration): SupportSQLiteOpenHelper {
                return SQLiteOpenHelper(
                    context,
                    config.name,
                    passphrase,
                    null,
                    object : SQLiteDatabaseHook {
                        override fun preKey(database: SQLiteDatabase?) {}
                        override fun postKey(database: SQLiteDatabase?) {}
                    }
                )
            }
        }
        
        return Room.databaseBuilder(context, AppDatabase::class.java, "game-center-db")
            .openHelperFactory(factory)
            .build()
    }

    @Provides
    fun provideIDownloadListDb(appDatabase: AppDatabase): IDownloadListDb {
        return appDatabase.downloadListDb()
    }

    @Provides
    fun providePlayRecordDao(appDatabase: AppDatabase): PlayRecordDao {
        return appDatabase.playRecordDao()
    }
}
