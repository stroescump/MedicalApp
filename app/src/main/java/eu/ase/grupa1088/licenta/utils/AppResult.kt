package eu.ase.grupa1088.licenta.utils

sealed class AppResult<out T> {
    object Progress : AppResult<Nothing>()
    data class Success<out T>(val successData: T?) : AppResult<T>()
    data class Error<out T>(val exception: Throwable) : AppResult<T>()
}