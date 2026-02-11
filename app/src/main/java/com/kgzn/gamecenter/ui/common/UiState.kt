package com.kgzn.gamecenter.ui.common

/**
 * Unified UI state representation following MVI pattern
 * Use this sealed interface for consistent state management across ViewModels
 */
sealed interface UiState<out T> {
    /**
     * Initial or loading state
     */
    data object Loading : UiState<Nothing>

    /**
     * Success state with data
     */
    data class Success<T>(val data: T) : UiState<T>

    /**
     * Error state with message
     */
    data class Error(val message: String, val throwable: Throwable? = null) : UiState<Nothing>

    /**
     * Empty state (no data available)
     */
    data object Empty : UiState<Nothing>
}

/**
 * Helper extension to check if state is loading
 */
fun <T> UiState<T>.isLoading(): Boolean = this is UiState.Loading

/**
 * Helper extension to check if state is success
 */
fun <T> UiState<T>.isSuccess(): Boolean = this is UiState.Success

/**
 * Helper extension to check if state is error
 */
fun <T> UiState<T>.isError(): Boolean = this is UiState.Error

/**
 * Helper extension to get data if success, null otherwise
 */
fun <T> UiState<T>.dataOrNull(): T? = (this as? UiState.Success)?.data
