package com.hector.koes

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform