package lzdev42.kotlintoolbox

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform