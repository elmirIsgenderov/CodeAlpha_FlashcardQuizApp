package com.example.flashcardquizapp.di

import android.content.Context
import androidx.room.Room
import com.example.flashcardquizapp.room.AppDatabase
import com.example.flashcardquizapp.room.FlashcardDao
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
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "flashcard_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideFlashcardDao(db: AppDatabase): FlashcardDao {
        return db.flashcardDao()
    }

}