# Undefined RunServer [![](https://dcbadge.limes.pink/api/server/https://discord.gg/NtWa9e3vv3?style=flat)](https://discord.gg/NtWa9e3vv3)

This gradle plugin is made to be able to run any minecraft server type inside of you IDE. (See below for all supported jars)
* SPIGOT
* CRAFTBUKKIT
* PAPERMC
* PUFFERFISH
* PURPUR
* BUNGEECORD
* WATERFALL
* VELOCITY
* FOLIA
* AdvancedSlimePaper (ASP)

## Imports
To import the plugin you will need to add this to your build.gradle.kts
```
id("com.undefinedcreation.nova") version "0.0.5"
```

## Configuration
There are multiple configurations that you can use (See below).
```
runServer {
    // Minecraft server its running (Do check if the version does exist)
    minecraftVersion("1.20.4")

    // Change the amount of ram allowed by the server
    allowedRam("4G")
        
    // If there should be a gui
    noGui(true)
        
    // Accept the mojang eula
    acceptMojangEula(true)
}
```

## Questions
If you have any questions or need help using this plugin please join the [Discord server](https://discord.gg/NtWa9e3vv3).
