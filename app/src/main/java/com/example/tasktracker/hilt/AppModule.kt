package com.example.tasktracker.hilt

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.tasktracker.roomdatabase.ScheduledTaskDao
import com.example.tasktracker.roomdatabase.ScheduledTaskDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule() {

    @Provides
    @Singleton
    fun provideContext(application: Application): Context = application

    @Provides
    @Singleton
    fun provideScheduledTaskDatabase(context: Context): ScheduledTaskDatabase {
        return Room.databaseBuilder(
            context,
            ScheduledTaskDatabase::class.java,
            "Scheduled Task database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideDao(database: ScheduledTaskDatabase): ScheduledTaskDao {
        return database.scheduledTaskDao()
    }


}