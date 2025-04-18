import com.undefinedcreations.nova.ServerType

plugins {
    kotlin("jvm") version "1.9.21"
    id("com.undefinedcreations.echo") version "0.0.10"
    id("com.undefinedcreations.nova")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.undefinedcreations"
version = "1.0.1"

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://oss.sonatype.org/content/repositories/central")
}

dependencies {
    echo("1.13", mojangMappings = false)
}

tasks {
    shadowJar {
        archiveFileName.set("server-1.0.0.jar")
    }
    compileKotlin {
        kotlinOptions.jvmTarget = "21"
    }
    compileJava {
        options.release.set(21)
    }
    runServer {
        noGui(true)
        perVersionFolder(true)
        acceptMojangEula()
        serverType(ServerType.SPIGOT)
    }
}

java {
    disableAutoTargetJvm()
}

kotlin {
    jvmToolchain(21)
}