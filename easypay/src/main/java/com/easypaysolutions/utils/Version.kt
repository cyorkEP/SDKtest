package com.easypaysolutions.utils

internal enum class VersionType {
    ACTUAL,         //version is matching
    DEPRECATED,     //minor version y differs
    OUTDATED        //major version x differs
}

internal class VersionManager {
    private val currentVersion = "1.1.6"
    private val minVersion = "1.0.0"

    fun getCurrentSdkVersion(): String = currentVersion

    fun checkVersionType(): VersionType {
        val libVersion = currentVersion

        if (compareMajorVersions(libVersion, minVersion) == VersionType.OUTDATED) {
            return VersionType.OUTDATED
        }

        if (compareMinorVersions(libVersion, minVersion) == VersionType.DEPRECATED) {
            return VersionType.DEPRECATED
        }

        return VersionType.ACTUAL
    }

    private fun compareMajorVersions(libVersion: String, minVersion: String): VersionType {
        val libVersionComponents = libVersion.split("")
        val minVersionComponents = minVersion.split("")

        val libMajorVersion = libVersionComponents.first().toIntOrNull() ?: 0
        val minLibMajorVersion = minVersionComponents.first().toIntOrNull() ?: 0

        return if (libMajorVersion < minLibMajorVersion) {
            VersionType.OUTDATED
        } else {
            VersionType.ACTUAL
        }
    }

    private fun compareMinorVersions(libVersion: String, minVersion: String): VersionType {
        val libVersionComponents = libVersion.split("")
        val minVersionComponents = minVersion.split("")

        val libMinorVersion = libVersionComponents.first().toIntOrNull() ?: 0
        val minLibMinorVersion = minVersionComponents.first().toIntOrNull() ?: 0

        return if (libMinorVersion < minLibMinorVersion) {
            VersionType.DEPRECATED
        } else {
            VersionType.ACTUAL
        }
    }
}
