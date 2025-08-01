package com.example.flashcardquizapp.room

import javax.inject.Inject

class Repository @Inject constructor(private val flashcardDao: FlashcardDao) {

    suspend fun getAll() =flashcardDao.getAll()
    suspend fun insert(flashcard: Flashcard) = flashcardDao.insert(flashcard)
    suspend fun update(flashcard: Flashcard) = flashcardDao.update(flashcard)
    suspend fun delete(flashcard: Flashcard) = flashcardDao.delete(flashcard)
}