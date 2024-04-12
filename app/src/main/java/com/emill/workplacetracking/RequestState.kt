package com.emill.workplacetracking

sealed class RequestState<out T> {
    object Empty : RequestState<Nothing>()
    object Loading : RequestState<Nothing>()
    data class Success<T>(val data: T) : RequestState<T>()
    data class Error(val exception: Exception) : RequestState<Nothing>()
}
