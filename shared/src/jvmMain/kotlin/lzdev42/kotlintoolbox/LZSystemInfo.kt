package lzdev42.kotlintoolbox

actual object LZSystemInfo {
    actual fun get(): SystemInfo {
        return SystemInfo(
            osName = System.getProperty("os.name") ?: "Unknown",
            osVersion = System.getProperty("os.version") ?: "Unknown",
            arch = System.getProperty("os.arch") ?: "Unknown",
            model = "Java VM" // JVM 上很难获取具体硬件型号，通常是通用的
        )
    }
}
