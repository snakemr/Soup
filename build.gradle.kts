import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

group = "ru.lrmk"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://packages.jetbrains.team/maven/p/kds/kotlin-ds-maven")
    google()
}


dependencies {
    val jsoup: String by project
    val dataframe: String by project
    val kandy: String by project
    val statistics: String by project

    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    implementation("org.jsoup:jsoup:$jsoup")
    implementation("org.jetbrains.kotlinx:dataframe-core:$dataframe")
    implementation("org.jetbrains.kotlinx:kandy-lets-plot:$kandy")
    implementation("org.jetbrains.kotlinx:kotlin-statistics-jvm:$statistics")
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "soup"
            packageVersion = "1.0.0"
        }
    }
}
