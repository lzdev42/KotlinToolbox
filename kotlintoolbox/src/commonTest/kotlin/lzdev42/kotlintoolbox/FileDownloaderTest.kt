package lzdev42.kotlintoolbox

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlin.test.Test
import kotlin.test.assertTrue

class FileDownloaderTest {

    @Test
    fun testDownload() = runTest {
        val client = HttpClient(CIO)
        val downloader = LZFileDownloader(client)
        
        val url = "https://www.google.com/robots.txt"
        // kotlinx-io doesn't have a direct SYSTEM_TEMPORARY_DIRECTORY equivalent in common yet easily accessible without expect/actual,
        // but for JVM test we can use java.io.tmpdir or just assume a temp path.
        // Actually, let's use a relative path for simplicity in test or try to find a temp dir.
        // For now, let's use the current directory's build folder which is safe.
        val buildDir = Path("build")
        val destPath = Path(buildDir, "robots.txt")
        
        val fileSystem = SystemFileSystem

        if (fileSystem.exists(destPath)) {
            fileSystem.delete(destPath)
        }

        val statuses = downloader.download(url, destPath.toString()).toList()
        
        assertTrue(statuses.isNotEmpty(), "Statuses should not be empty")
        assertTrue(statuses.first() is DownloadStatus.Pending, "First status should be Pending")
        assertTrue(statuses.any { it is DownloadStatus.Downloading }, "Should have Downloading status")
        assertTrue(statuses.last() is DownloadStatus.Completed, "Last status should be Completed")
        
        assertTrue(fileSystem.exists(destPath), "File should exist")
        val metadata = fileSystem.metadataOrNull(destPath)
        assertTrue(metadata != null && metadata.size > 0, "File size should be > 0")
        
        fileSystem.delete(destPath)
    }
}
