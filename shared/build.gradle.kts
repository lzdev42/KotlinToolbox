import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    id("maven-publish")
}

group = "lzdev42"
version = "0.2.3"

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64(),
        macosArm64(),
        macosX64()
    ).forEach { appleTarget ->
        appleTarget.binaries.framework {
            baseName = "KotlinToolbox"
            isStatic = true
        }
    }

    jvm()

    sourceSets.all {
        languageSettings.optIn("kotlin.experimental.ExperimentalObjCName")
    }
    


    sourceSets {
        commonMain.dependencies {
            // put your Multiplatform dependencies here
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.cio)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.logging)
            implementation(libs.kotlinx.io.core)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}

android {
    namespace = "lzdev42.kotlintoolbox"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}



publishing {
    publications.withType<MavenPublication> {
        // KMP 插件会自动为每个平台创建 publication
        // 我们只需要配置共同的 pom 信息
        pom {
            name.set("KotlinToolbox")
            description.set("A Kotlin Multiplatform utility library")
            url.set("https://github.com/lzdev42/KotlinToolbox")
            
            licenses {
                license {
                    name.set("MIT License")
                    url.set("https://opensource.org/licenses/MIT")
                }
            }
            
            developers {
                developer {
                    id.set("lzdev42")
                    name.set("lzdev42")
                }
            }
            
            scm {
                connection.set("scm:git:git://github.com/lzdev42/KotlinToolbox.git")
                developerConnection.set("scm:git:ssh://github.com/lzdev42/KotlinToolbox.git")
                url.set("https://github.com/lzdev42/KotlinToolbox")
            }
        }
    }
}

// 修改 artifactId 从 shared 改为 kotlintoolbox
afterEvaluate {
    publishing {
        publications.all {
            val targetPublication = this as? MavenPublication
            targetPublication?.artifactId = targetPublication?.artifactId?.replace("shared", "kotlintoolbox")
        }
    }
}

