plugins {
    kotlin("jvm") version "1.9.21"
    id("maven-publish")
    id("com.gradle.plugin-publish") version "1.2.1"
    id("org.jetbrains.dokka") version "2.0.0"
}

group = "com.undefinedcreations"
version = "0.1.6"

repositories {
    mavenCentral()
}

dependencies {
    api(kotlin("stdlib"))
    implementation("com.google.code.gson:gson:2.11.0")
}

gradlePlugin {

    website.set("https://discord.undefinedcreations.com/")
    vcsUrl.set("https://github.undefinedcreations.com/")

    plugins {
        create("runServer") {
            id = "com.undefinedcreations.runServer"
            displayName = "Undefined run server"
            description = "This gradle plugin allows you to run different type of minecraft servers in your Intellij"
            tags = listOf("spigot", "mapping", "NMS", "mojang", "utils", "server", "runServer", "paper", "pufferfishmc", "purpur", "bungeecord", "waterfall")
            implementationClass = "com.undefinedcreations.runServer.RunServerPlugin"
        }
    }

}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileJava {
        options.release.set(8)
    }
}


kotlin {
    jvmToolchain(8)
}