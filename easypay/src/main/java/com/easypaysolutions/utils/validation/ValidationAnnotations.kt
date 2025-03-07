package com.easypaysolutions.utils.validation

@Retention
@Target(AnnotationTarget.FIELD)
internal annotation class ValidateLength(val maxLength: Int)

@Retention
@Target(AnnotationTarget.FIELD)
internal annotation class ValidateRegex(val regex: String)

@Retention
@Target(AnnotationTarget.FIELD)
internal annotation class ValidateNumberGreaterThanZero

@Retention
@Target(AnnotationTarget.FIELD)
internal annotation class ValidateNotBlank