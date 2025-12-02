package lzdev42.kotlintoolbox

import platform.Foundation.NSProcessInfo
import platform.posix.uname
import platform.posix.utsname
import kotlinx.cinterop.*

@OptIn(ExperimentalForeignApi::class)
actual object LZSystemInfo {
    actual fun get(): SystemInfo {
        val processInfo = NSProcessInfo.processInfo
        
        // 获取架构信息
        val arch = memScoped {
            val systemInfo = alloc<utsname>()
            uname(systemInfo.ptr)
            systemInfo.machine.toKString()
        }

        return SystemInfo(
            osName = "macOS",
            osVersion = processInfo.operatingSystemVersionString,
            arch = arch,
            model = "Mac"
        )
    }
}
