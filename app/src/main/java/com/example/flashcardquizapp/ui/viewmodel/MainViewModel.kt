package com.example.flashcardquizapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcardquizapp.room.Repository
import com.example.flashcardquizapp.utils.Resource
import com.example.flashcardquizapp.room.Flashcard
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor (private val repository: Repository) : ViewModel() {

    private val _getAll = MutableLiveData<Resource<List<Flashcard>>>()
    val getAll: LiveData<Resource<List<Flashcard>>>
        get() = _getAll

    private val _insert = MutableLiveData<Resource<Unit>>()
    val insert: LiveData<Resource<Unit>>
        get() = _insert

    private val _update = MutableLiveData<Resource<Unit>>()
    val update: LiveData<Resource<Unit>>
        get() = _update

    private val _delete = MutableLiveData<Resource<Unit>>()
    val delete: LiveData<Resource<Unit>>
        get() = _delete

    fun getAll() {
        viewModelScope.launch(Dispatchers.IO) {
            _getAll.postValue(Resource.Loading)
            try {
                val data = repository.getAll()
                _getAll.postValue(Resource.Success(data))
            } catch (e: Exception) {
                _getAll.postValue(Resource.Error(e.message))
            }
        }
    }

    fun insert(flashcard: Flashcard) {
        viewModelScope.launch(Dispatchers.IO) {
            _insert.postValue(Resource.Loading)
            try {
                repository.insert(flashcard)
                _insert.postValue(Resource.Success(Unit))
            } catch (e: Exception) {
                _insert.postValue(Resource.Error(e.message))
            }
        }
    }

    fun update(flashcard: Flashcard) {
        viewModelScope.launch(Dispatchers.IO) {
            _update.postValue(Resource.Loading)
            try {
                repository.update(flashcard)
                _update.postValue(Resource.Success(Unit))
            } catch (e: Exception) {
                _update.postValue(Resource.Error(e.message))
            }
        }
    }

    fun delete(flashcard: Flashcard) {
        viewModelScope.launch(Dispatchers.IO) {
            _delete.postValue(Resource.Loading)
            try {
                repository.delete(flashcard)
                _delete.postValue(Resource.Success(Unit))
            } catch (e: Exception) {
                _delete.postValue(Resource.Error(e.message))
            }
        }
    }
}
