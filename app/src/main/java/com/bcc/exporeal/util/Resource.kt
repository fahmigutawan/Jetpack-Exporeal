package com.bcc.exporeal.util

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null,
    val bytesTransferred: Long? = null,
    val totalBytes: Long? = null
) {
    class Success<T>(data: T?) : Resource<T>(data)
    class Loading<T>(data: T? = null, bytesTransferred: Long? = null, totalBytes: Long? = null) : Resource<T>(data, bytesTransferred = bytesTransferred, totalBytes = totalBytes)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
}