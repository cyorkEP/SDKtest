package com.easypaysolutions.utils.feature_flags

internal interface FeatureFlagManager {
    fun isRootedDeviceDetectionEnabled(): Boolean
    fun setRootedDeviceDetectionEnabled(enabled: Boolean)
}