package lzdev42.kotlintoolbox

import android.os.Build

actual object LZSystemInfo {
    actual fun get(): SystemInfo {
        return SystemInfo(
            osName = "Android",
            osVersion = Build.VERSION.RELEASE, // e.g., "14"
            arch = Build.SUPPORTED_ABIS.firstOrNull() ?: "Unknown", // e.g., "arm64-v8a"
            model = "${Build.MANUFACTURER} ${Build.MODEL}" // e.g., "Google Pixel 8"
        )
    }
}
