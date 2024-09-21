import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import java.util.Properties

plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.0"
}

group = "me.tud.skriptscoreboards"
version = "1.0.0"

repositories {
    mavenCentral()
    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        url = uri("https://repo.skriptlang.org/releases/")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    compileOnly("com.github.SkriptLang:Skript:2.9.2")
    compileOnly("org.eclipse.jdt:org.eclipse.jdt.annotation:2.3.0")
    implementation("fr.mrmicky:fastboard:2.1.3")
}

val properties = Properties()
if (file("local.properties").exists()) {
    properties.load(file("local.properties").inputStream())
}
val jarName: String? = properties.getProperty("jarName")
val jarDir: String? = properties.getProperty("jarDir")

tasks.withType<Jar> {
    archiveFileName.set(jarName ?: "${project.name}-${project.version}.jar")
    if (jarDir != null)
        destinationDirectory.set(project.file(jarDir))
}

tasks.withType<ShadowJar> {
    relocate("fr.mrmicky.fastboard", "me.tud.skriptscoreboards.fastboard")
}

tasks.processResources {
    expand(mapOf(
        "name" to project.name,
        "version" to project.version,
        "group" to project.group
    ))
    filteringCharset = "UTF-8"
}
