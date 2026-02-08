package com.kgzn.gamecenter.ui.search

import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kgzn.gamecenter.data.AppApi
import com.kgzn.gamecenter.data.Search2
import com.kgzn.gamecenter.data.local.LocalAppApiImpl
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class SearchViewModel(
    private val appApi: AppApi = LocalAppApiImpl(),
    private val snackbarHostState: SnackbarHostState,
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
            viewModelScope.launch {
                appApi.search2(_key.value).catch { throwable ->
                    Log.e(TAG, "search: ", throwable)
                    _loading.value = false
                    throwable.localizedMessage?.let { viewModelScope.launch { snackbarHostState.showSnackbar(it) } }
                }.collect { results ->
                    _result.value = results
                    _loading.value = false
                }
            }
        }
    }

    fun resetIfNeed() {
        if (_result.value?.isEmpty() == true) {
            _result.value = null
        }
    }
}
