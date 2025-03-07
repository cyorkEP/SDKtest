package com.easypaysolutions.utils.feature_flags

object EasyPayFeatureFlagManager: FeatureFlagManager {

    private var isRootedDeviceDetectionEnabled = false

    override fun isRootedDeviceDetectionEnabled(): Boolean = isRootedDeviceDetectionEnabled

    override fun setRootedDeviceDetectionEnabled(enabled: Boolean) {
        isRootedDeviceDetectionEnabled = enabled
    }
}