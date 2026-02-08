package com.kgzn.gamecenter.ui.home

import android.content.Context
import android.util.Log
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kgzn.gamecenter.R
import com.kgzn.gamecenter.data.AppApi
import com.kgzn.gamecenter.data.ContentConfig
import com.kgzn.gamecenter.data.local.LocalAppApiImpl
import com.kgzn.gamecenter.feature.downloader.utils.mapStateFlow
import com.kgzn.gamecenter.ui.GcAppState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private data class HomeBarState(
    val pagerState: PagerState,
    val actions: List<Pair<String?, Any?>>,
)

class HomeViewModel(
    private val appApi: AppApi = LocalAppApiImpl(),
    appState: GcAppState,
    private val snackbarHostState: SnackbarHostState = appState.snackbarHostState,
    context: Context,
) : ViewModel() {

    companion object {
        private const val TAG = "HomeViewModel"
    }

    private val _contentConfigs = MutableStateFlow(emptyList<ContentConfig>())

    private val _loading = MutableStateFlow(true)

    val contentConfigs: StateFlow<List<ContentConfig>> = _contentConfigs

    val loading: StateFlow<Boolean> = _loading

    private val _homeBarState =
        _contentConfigs.map { it.map { listOf(it.name, it.selIcon, it.isHome) } }.distinctUntilChanged { old, new ->
            old.size == new.size && old.indexOfFirst { it[2] == 1 } == new.indexOfFirst { it[2] == 1 }
        }.map { data ->
            Log.d(TAG, "pagerState: $data")
            val size = data.size
            val homeIndex = data.indexOfFirst { it[2] == 1 }
            HomeBarState(
                pagerState = PagerState(homeIndex + 1) { size + 1 },
                actions = listOf(context.getString(R.string.mine) to R.drawable.ic_mine) + data.map { it[0] as String? to it[1] as Any? }
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = HomeBarState(
                pagerState = PagerState(0) { 1 },
                actions = listOf(context.getString(R.string.mine) to R.drawable.ic_mine),
            )
        )

    val pagerState = _homeBarState.mapStateFlow { it.pagerState }
    val actions: StateFlow<List<Pair<String?, Any?>>> = _homeBarState.mapStateFlow { it.actions }

    fun fetchContentConfigs() {
        viewModelScope.launch {
            appApi.getAllContentConfigs().catch { e ->
                Log.e(TAG, "getAllContentConfigs error: ${e.localizedMessage}", e)
                _loading.value = false
                e.localizedMessage?.let { viewModelScope.launch { snackbarHostState.showSnackbar(it) } }
            }.collect { contentConfigs ->
                _loading.value = false
                _contentConfigs.value = contentConfigs
            }
        }
    }

}
