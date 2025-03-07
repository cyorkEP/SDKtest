package com.easypaysolutions.repositories

import java.lang.reflect.Field

abstract class BaseBodyParams {
    internal abstract fun toMappedFields(): List<MappedField>
}

internal class MappedField(
    val field: Field,
    val value: Any?,
)