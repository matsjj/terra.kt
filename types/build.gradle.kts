plugins {
    kotlin("plugin.serialization") version "1.6.0"
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                val kotlinxSerializationVersion: String by project

                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:$kotlinxSerializationVersion")
            }
        }
    }
}
