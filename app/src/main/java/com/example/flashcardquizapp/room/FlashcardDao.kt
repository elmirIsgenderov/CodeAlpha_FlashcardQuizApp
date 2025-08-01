package com.example.flashcardquizapp.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface FlashcardDao {

    @Query("SELECT * FROM flashcards")
    suspend fun getAll(): List<Flashcard>

    @Insert
    suspend fun insert(flashcard: Flashcard)

    @Update
    suspend fun update(flashcard: Flashcard)

    @Delete
    suspend fun delete(flashcard: Flashcard)
}