plugins {
    kotlin("jvm") version "2.0.0"
    id("maven-publish")
    id("com.gradle.plugin-publish") version "1.2.1"
}

group = "com.undefinedcreation"
version = "0.0.9"

repositories {
    mavenCentral()
}

dependencies {
    api(kotlin("stdlib"))
    implementation("com.google.code.gson:gson:2.11.0")
}

gradlePlugin {

    website.set("https://discord.gg/NtWa9e3vv3")
    vcsUrl.set("https://github.com/UndefinedCreation/UndefinedRunServer")

    plugins {
        create("runServer") {
            id = "com.undefinedcreation.runServer"
            displayName = "Undefined run server"
            description = "This gradle plugin allows you to run different type of minecraft servers in your Intellij"
            tags = listOf("spigot", "mapping", "NMS", "mojang", "utils", "server", "runServer", "paper", "pufferfishmc", "purpur", "bungeecord", "waterfall")
            implementationClass = "com.undefinedcreation.runServer.RunServerPlugin"
        }
    }

}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}