package lzdev42.kotlintoolbox

import platform.UIKit.UIDevice
import platform.Foundation.NSProcessInfo
import platform.posix.uname
import platform.posix.utsname
import kotlinx.cinterop.*

@OptIn(ExperimentalForeignApi::class)
actual object LZSystemInfo {
    actual fun get(): SystemInfo {
        val device = UIDevice.currentDevice
        
        // 获取架构信息
        val arch = memScoped {
            val systemInfo = alloc<utsname>()
            uname(systemInfo.ptr)
            systemInfo.machine.toKString()
        }

        return SystemInfo(
            osName = device.systemName, // e.g., "iOS"
            osVersion = device.systemVersion, // e.g., "17.2"
            arch = arch, // e.g., "arm64"
            model = device.model // e.g., "iPhone", 具体型号需要查表，这里先返回通用型号
        )
    }
}
