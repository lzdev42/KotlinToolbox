package lzdev42.kotlintoolbox

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertNotNull

class LZSystemInfoTest {
    @Test
    fun testGetSystemInfo() {
        val info = LZSystemInfo.get()
        
        println("System Info: $info")
        
        assertNotNull(info.osName, "OS Name should not be null")
        assertNotNull(info.osVersion, "OS Version should not be null")
        assertNotNull(info.arch, "Architecture should not be null")
        assertNotNull(info.model, "Model should not be null")
        
        assertTrue(info.osName.isNotEmpty(), "OS Name should not be empty")
        assertTrue(info.osVersion.isNotEmpty(), "OS Version should not be empty")
        assertTrue(info.arch.isNotEmpty(), "Architecture should not be empty")
    }
}
