plugins {
    kotlin("multiplatform") version "1.3.61"
}


repositories {
    mavenCentral()
    jcenter()
    maven(url = "https://kotlin.bintray.com/kotlin-js-wrappers")
}

kotlin {
    js {
        browser {
            //            runTask {
//                outputFileName = "browser.js"
//                devServer = org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig.DevServer(
//                        contentBase = listOf("$buildDir/processedResources/js/main"),
//                        port = 8080
//                )
//            }
        }
    }
    jvm()
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-common:1.3.3")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.3")
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))
                val reactVersion = "16.9.0"
                val kotlinWrapperVersion = "pre.89-kotlin-1.3.60"
                api("org.jetbrains.kotlinx:kotlinx-html-js:0.6.12")
                api("org.jetbrains:kotlin-react-dom:$reactVersion-$kotlinWrapperVersion")
                api("org.jetbrains:kotlin-react:$reactVersion-$kotlinWrapperVersion")
                implementation("org.jetbrains:kotlin-styled:1.0.0-$kotlinWrapperVersion")
                implementation(npm("react", "^$reactVersion"))
                implementation(npm("react-dom", "^$reactVersion"))
                implementation(npm("inline-style-prefixer", "^5.1.0"))
                implementation(npm("styled-components", "^4.3.2"))

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:1.3.3")
            }
        }

    }
}


