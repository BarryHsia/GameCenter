package com.kgzn.gamecenter.ui.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kgzn.gamecenter.data.Search2
import com.kgzn.gamecenter.data.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val gameRepository: GameRepository,
) : ViewModel() {
    companion object {
        private const val TAG = "SearchViewModel"
    }

    private val _key = MutableStateFlow("")
    val key: StateFlow<String> = _key

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _result: MutableStateFlow<List<Search2>?> = MutableStateFlow(null)
    val results: StateFlow<List<Search2>?> = _result

    private val _snackbarMessage = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val snackbarMessage = _snackbarMessage.asSharedFlow()

    private var searchJob: Job? = null

    fun setKey(key: String) {
        _key.value = key
    }

    fun search(delay: Long) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(delay)
            if (_key.value.isBlank()) {
                _result.value = listOf()
                return@launch
            }

            _loading.value = true
            gameRepository.search(_key.value).catch { throwable ->
                Log.e(TAG, "search: ", throwable)
                _loading.value = false
                throwable.localizedMessage?.let { _snackbarMessage.tryEmit(it) }
            }.collect { results ->
                _result.value = results
                _loading.value = false
            }
        }
    }

    fun resetIfNeed() {
        if (_result.value?.isEmpty() == true) {
            _result.value = null
        }
    }
}
