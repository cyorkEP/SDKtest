package com.easypaysolutions.utils.validation

internal sealed interface ValidationState {
    data object NotFilled : ValidationState
    data object InternalValidationInvalid : ValidationState
    data object ExternalValidationInvalid : ValidationState
    data object Valid : ValidationState
}