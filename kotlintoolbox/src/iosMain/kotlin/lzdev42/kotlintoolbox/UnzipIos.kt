package lzdev42.kotlintoolbox

/**
 * iOS 平台的解压缩实现
 * 
 * ⚠️ iOS 平台暂不支持 ZIP 解压缩功能
 * 
 * 原因：
 * - Foundation 框架没有内置的 ZIP 解压缩 API
 * - 需要集成第三方库（如 SSZipArchive）
 * 
 * 建议：
 * 如果需要在 iOS 上解压缩文件，请在 Swift/Objective-C 代码中
 * 使用原生库（如 SSZipArchive、ZipArchive 等）
 */
actual fun unzipPlatform(zipFilePath: String): FileResult<String> {
    return FileResult.Error(
        "iOS 平台暂不支持 ZIP 解压缩，请在 Swift/Objective-C 中使用 SSZipArchive 等原生库",
        ErrorCode.UNSUPPORTED_PLATFORM
    )
}
