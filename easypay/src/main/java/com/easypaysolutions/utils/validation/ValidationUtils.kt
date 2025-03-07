package com.easypaysolutions.utils.validation

import com.easypaysolutions.api.responses.base.ApiResult
import com.easypaysolutions.exceptions.EasyPayApiException
import com.easypaysolutions.networking.NetworkResource
import com.easypaysolutions.repositories.BaseBodyParams
import com.easypaysolutions.repositories.MappedField

internal object RegexPattern {
    const val FIRST_OR_LAST_NAME = "^[a-zA-Z0-9'\\s\\-.,&?/]*$"
    const val ADDRESS1 = "^[a-zA-Z0-9\\-.,#_/'\\s]*$"
    const val ADDRESS2 = "^[a-zA-Z0-9\\-.,#_&/'\\s]*$"
    const val COMPANY = "^[a-zA-Z0-9\\-.,#_&/\\s]*$"
    const val CITY = "^[a-zA-Z .]*$"
    const val ZIP_CODE = "^[a-zA-Z0-9- ]*$"
    const val COUNTRY_OR_STATE = "^[a-zA-Z\\s]*$"
    const val SERVICE_DESCRIPTION = "^[a-zA-Z0-9 ._\\-#]*$"
    const val CLIENT_REF_ID_OR_RPGUID = "^[a-zA-Z0-9 ._,=&\\-#]*$"
    const val EMAIL = ".+@([A-Za-z0-9-]+\\.)+[A-Za-z]{2}[A-Za-z]*"
    const val ONLY_NUMBERS = "^[0-9]*$"
}

internal object ValidationErrorMessages {
    const val EITHER_RPGUID_OR_CUSTOMER_REFERENCE_ID = "Either RPGUID or Customer Reference ID must be provided"
}

internal object ValidatorUtils {

    suspend fun <T : ApiResult> validate(
        params: BaseBodyParams,
        call: suspend () -> NetworkResource<T>,
    ): NetworkResource<T> {
        val validationError = validate(params)
        validationError?.let {
            return NetworkResource.error(it)
        }
        return call()
    }

    fun <T : BaseBodyParams> validate(dataClass: T): EasyPayApiException? {
        dataClass.toMappedFields().forEach { mappedField ->
            mappedField.field.annotations.forEach { annotation ->
                when (annotation) {
                    is ValidateLength -> {
                        validateLength(mappedField, annotation)?.let { return it }
                    }

                    is ValidateRegex -> {
                        validateRegex(mappedField, annotation)?.let { return it }
                    }

                    is ValidateNumberGreaterThanZero -> {
                        validateNumberGreaterThanZero(mappedField)?.let { return it }
                    }

                    is ValidateNotBlank -> {
                        validateNotBlank(mappedField)?.let { return it }
                    }
                }
            }
        }
        return null
    }

    //region Data validations

    fun validateAtLeastOneNotBlank(
        data: List<String?>,
        errorMessage: String,
    ): EasyPayApiException? {
        val isAnyNotBlank = data.any { it?.isNotBlank() == true }
        return if (!isAnyNotBlank) {
            EasyPayApiException(
                message = errorMessage
            )
        } else {
            null
        }
    }

    //endregion

    //region MappedField validations

    private fun validateNotBlank(mappedField: MappedField): EasyPayApiException? {
        var isValid = true
        if (mappedField.value is String) {
            isValid = mappedField.value.isNotBlank()
        }
        return if (!isValid) {
            EasyPayApiException(
                message = "${mappedField.field.name} cannot be blank"
            )
        } else {
            null
        }
    }

    private fun validateNumberGreaterThanZero(mappedField: MappedField): EasyPayApiException? {
        var isValid = true
        if (mappedField.value is Double) {
            isValid = mappedField.value > 0
        }
        if (mappedField.value is Int) {
            isValid = mappedField.value > 0
        }
        return if (!isValid) {
            EasyPayApiException(
                message = "${mappedField.field.name} must be greater than 0"
            )
        } else {
            null
        }
    }

    private fun validateRegex(
        mappedField: MappedField,
        annotation: ValidateRegex,
    ): EasyPayApiException? {
        var isValid = true
        if (mappedField.value is String) {
            isValid = mappedField.value.matches(Regex(annotation.regex))
        }
        return if (!isValid) {
            EasyPayApiException(
                message = "${mappedField.field.name} contains invalid characters"
            )
        } else {
            null
        }
    }

    private fun validateLength(
        mappedField: MappedField,
        annotation: ValidateLength,
    ): EasyPayApiException? {
        var isValid = true
        if (mappedField.value is String) {
            isValid = mappedField.value.length <= annotation.maxLength
        }
        if (mappedField.value is Int) {
            isValid = mappedField.value.toString().length <= annotation.maxLength
        }
        return if (!isValid) {
            EasyPayApiException(
                message = "${mappedField.field.name} exceeds maximum length of ${annotation.maxLength} characters"
            )
        } else {
            null
        }
    }

    //endregion

}