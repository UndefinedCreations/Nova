plugins {
    kotlin("jvm") version "1.9.21"
    id("maven-publish")
    id("com.gradle.plugin-publish") version "1.2.1"
    id("com.gradleup.shadow") version "8.3.6"
}

group = properties["group"]!!
version = properties["version"]!!

repositories {
    mavenCentral()
}

dependencies {
    api(kotlin("stdlib"))
    implementation("net.md-5:SpecialSource:1.11.4")
    implementation("com.google.code.gson:gson:2.12.1")
}


gradlePlugin {
    website = "https://discord.undefinedcreations.com/"
    vcsUrl = "https://github.com/UndefinedCreations/UndefinedRunServer"

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
    shadowJar {
        archiveClassifier = ""
    }
}