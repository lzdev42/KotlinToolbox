package lzdev42.kotlintoolbox

/**
 * macOS 平台的解压缩实现
 * 
 * ⚠️ macOS 平台暂不支持 ZIP 解压缩功能
 * 
 * 原因：
 * - 需要使用 Foundation 或第三方库
 * - 当前实现保持简单
 * 
 * 建议：
 * 如果需要在 macOS 上解压缩文件，请在 Swift 代码中
 * 使用 Foundation 的 FileManager 或第三方库
 */
actual fun unzipPlatform(zipFilePath: String): FileResult<String> {
    return FileResult.Error(
        "macOS 平台暂不支持 ZIP 解压缩，请在 Swift 中使用 Foundation 或第三方库",
        ErrorCode.UNSUPPORTED_PLATFORM
    )
}
