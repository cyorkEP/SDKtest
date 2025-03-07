package com.easypaysolutions.exceptions

/**
 * [EasyPaySdkException] - a class for exceptions issued in the SDK.
 */
class EasyPaySdkException(
    val type: Type,
    cause: Throwable? = null,
) : EasyPayException(null, type.message, cause) {

    enum class Type(val message: String) {
        EASY_PAY_CONFIGURATION_NOT_INITIALIZED("EasyPayConfiguration not initialized."),
        MISSED_SESSION_KEY("Missed Session Key."),
        MISSED_HMAC_SECRET("Missed HMAC secret."),
        ROOTED_DEVICE_DETECTED("Rooted device detected. EasyPay SDK does not support rooted devices."),
        RSA_CERTIFICATE_NOT_FETCHED("RSA certificate not fetched."),
        RSA_CERTIFICATE_FETCH_FAILED("RSA certificate fetch failed."),
        RSA_CERTIFICATE_PARSING_ERROR("RSA certificate parsing error."),
    }
}