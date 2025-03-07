package com.easypaysolutions.utils.secured

internal interface SecureWidget<T> {
    val secureData: SecureData<T>
}

data class SecureData<T> internal constructor(
    val data: T,
)