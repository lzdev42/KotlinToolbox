package lzdev42.kotlintoolbox

/**
 * 系统信息数据类
 */
data class SystemInfo(
    /** 操作系统名称 (e.g., "Android", "iOS", "Mac OS X", "Windows") */
    val osName: String,
    
    /** 操作系统版本 (e.g., "14.0", "17.2", "11") */
    val osVersion: String,
    
    /** CPU 架构 (e.g., "aarch64", "x86_64", "arm64") */
    val arch: String,
    
    /** 设备型号 (e.g., "iPhone 15", "Pixel 8", "Unknown") */
    val model: String
)

/**
 * 系统信息工具
 * 用于获取当前运行环境的系统信息
 */
expect object LZSystemInfo {
    /**
     * 获取当前系统信息
     */
    fun get(): SystemInfo
}
