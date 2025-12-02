package lzdev42.kotlintoolbox

/**
 * 平台特定的解压缩实现
 * @param zipFilePath ZIP 文件路径
 * @return FileResult<String> 成功返回解压目录路径，失败返回错误信息
 */
expect fun unzipPlatform(zipFilePath: String): FileResult<String>
