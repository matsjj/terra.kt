plugins {
    kotlin("plugin.serialization") version "1.6.0"
}

kotlin {
    sourceSets {
        all {
            languageSettings {
                optIn("kotlin.RequiresOptIn")
            }
        }

        val commonMain by getting {
            dependencies {
                val kotlinxCoroutineVersion: String by project
                val kotlinxSerializationVersion: String by project
                val commonUtilVersion: String by project

                api(project(":types"))
                api(project(":messages"))

                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion")

                implementation("kr.jadekim:common-encoder:$commonUtilVersion")
            }
        }
    }
}
