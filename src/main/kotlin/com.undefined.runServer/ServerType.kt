package com.undefined.runServer

import com.google.gson.JsonParser
import java.io.File
import java.io.FileOutputStream
import java.net.URI

enum class ServerType(val URL_BUILDVERSION: String, val URL_DOWNLOAD: String) {
    SPIGOT("", ""),
    CRAFTBUKKIT("", ""),
    PAPERMC("https://api.papermc.io/v2/projects/paper/versions/", "https://api.papermc.io/v2/projects/paper/versions/mcVersion/builds/newestBuild/downloads/paper-mcVersion-newestBuild.jar"),
    PUFFERFISH("as", ""),
    PURPUR("", ""),
    BUNGEECORD("", ""),
    WATERFALL("", ""),
    VELOCITY("", ""),
    FOLIA("", "")
}

fun ServerType.getLatestBuild(mcVersion: String): String {
    val request = URI(URL_BUILDVERSION + mcVersion)
    val jsonText = request.toURL().readText()
    val json = JsonParser.parseString(jsonText).asJsonObject
    val array = json.getAsJsonArray("builds")
    return array.get(array.size() - 1).asString
}

fun ServerType.download(folder: File, latestBuild: String, mcVersion: String) {

    val request = URI(URL_DOWNLOAD.replace("mcVersion", mcVersion).replace("newestBuild", latestBuild))
    val file = File(folder, "$name-$mcVersion-$latestBuild.jar")
    if (!file.exists()) {
        request.toURL().openStream().use { inputStream ->
            FileOutputStream(file).use { outStream ->
                inputStream.copyTo(outStream)
            }
        }
    }

}