package com.pointpadel.app.di

import android.content.Context
import androidx.room.Room
import com.pointpadel.app.data.local.PadelDatabase
import com.pointpadel.app.data.local.PartidoDao
import com.pointpadel.app.data.repository.MatchRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providePadelDatabase(@ApplicationContext context: Context): PadelDatabase {
        return Room.databaseBuilder(
            context,
            PadelDatabase::class.java,
            PadelDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    fun providePartidoDao(database: PadelDatabase): PartidoDao {
        return database.partidoDao()
    }

    @Provides
    @Singleton
    fun provideMatchRepository(partidoDao: PartidoDao): MatchRepository {
        return MatchRepository(partidoDao)
    }
}
