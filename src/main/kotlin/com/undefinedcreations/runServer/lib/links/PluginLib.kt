package com.undefinedcreations.runServer.lib.links

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.undefinedcreations.runServer.ServerType
import com.undefinedcreations.runServer.exception.UnsupportedPluginPlatform
import java.io.File
import java.net.URI

/**
 * Used to download and get information from plugins.
 *
 * @since 1.0
 */
object PluginLib {

    private const val CHARACTER_FILTER_REGEX = "[^\\p{L}\\p{N}\\p{P}\\p{Z}]"

    /**
     * Used to download a plugin from a `url` and `serverType`.
     *
     * @param folder The directory where the file will be saved.
     * @param url The url of the plugin.
     * @param serverType The type of server it will download.
     * @return The result of the download.
     * @throws UnsupportedPluginPlatform If the platform is not supported.
     */
    fun download(folder: File, url: String, serverType: ServerType): DownloadResult =
        when {
            url.contains("spigotmc.org/resources") -> spigot(folder, url)
            url.contains("hangar.papermc.io") -> paper(folder, url, serverType)
            url.contains("modrinth.com/plugin") -> modrinth(folder, url, serverType)
            else -> throw UnsupportedPluginPlatform(url)
        }

    /**
     * Used to get the file name for a plugin.
     *
     * @param url The url of the plugin.
     * @param serverType The type of server it will download.
     * @return The file name.
     * @throws UnsupportedPluginPlatform If the platform is not supported.
     */
    fun fileName(url: String, serverType: ServerType): String =
        when {
            url.contains("spigotmc.org/resources") -> spigotName(spigotID(url))
            url.contains("hangar.papermc.io") -> paperName(idFromUrl(url), paperVersion(idFromUrl(url)))
            url.contains("modrinth.com/plugin") -> modrinthName(url, serverType)!!
            else -> throw UnsupportedPluginPlatform(url)
        }

    private fun spigot(folder: File, url: String): DownloadResult {
        try {
            val id = spigotID(url)
            DownloadLib.downloadFile(folder, "${Repositories.SPIGOT_API}/$id/download", spigotName(id))
            return DownloadResult(DownloadResultType.SUCCESS, null, null)
        } catch (e: Exception) {
            return DownloadResult(DownloadResultType.FAILED, e.message, null)
        }
    }

    private fun spigotID(url: String) =
        url.split(".")[3].split("/")[0]

    private fun spigotName(id: String): String =
        JsonParser.parseString(URI("${Repositories.SPIGOT_API}/$id/").toURL().readText())
            .asJsonObject.run { "${
                cleanUpName(this["name"].asString)
            }-${
                this.get("versions").asJsonArray.last().asJsonObject["id"].asInt
            }.jar" }

    private fun paper(folder: File, url: String, serverType: ServerType): DownloadResult {
        try {
            val id = idFromUrl(url)
            val version = paperVersion(id)
            val downloadUrl = "https://hangar.papermc.io/api/v1/projects/$id/versions/$version/${if (serverType == ServerType.CUSTOM || !serverType.proxy && serverType != ServerType.FOLIA) "PAPER" else serverType.loaderName.uppercase()}/download"
            DownloadLib.downloadFile(folder, downloadUrl, paperName(id, version))
            return DownloadResult(DownloadResultType.SUCCESS, null, null)
        } catch (e: Exception) {
            return DownloadResult(DownloadResultType.FAILED, e.message, null)
        }
    }

    private fun paperName(id: String, version: String): String =
        "$id-$version.jar"

    private fun idFromUrl(url: String): String =
        url.split("/").last()

    private fun paperVersion(id: String): String =
        URI("${Repositories.PAPERMC_API}$id/latestrelease").toURL().readText()


    private fun modrinthJson(url: String, serverType: ServerType): JsonObject? {
        val id = idFromUrl(url)
        val apiUrl = URI("${Repositories.MODRINTH_API}$id/version")
        val versions = JsonParser.parseString(apiUrl.toURL().readText()).asJsonArray

        for (versionEntry in versions) {
            val json = versionEntry.asJsonObject

            if (isSnapshotVersion(json)) continue
            if (!isCompatibleWithServerType(json, serverType)) continue

            return json
        }
        return null
    }

    private fun modrinthName(url: String, serverType: ServerType): String? =
        modrinthJson(url, serverType)?.let { json ->
            "${json["name"].asString.replace(" ", "")}-${json["version_number"].asInt}.jar"
        }

    private fun modrinth(folder: File, url: String, serverType: ServerType): DownloadResult {
        try {
            val json = modrinthJson(url, serverType)
            if (json != null) {
                val fileJson = json["files"].asJsonArray.first().asJsonObject
                val downloadUrl = fileJson["url"].asString
                val fileName = "${json["name"].asString.replace(" ", "")}-${json["version_number"].asInt}.jar"
                DownloadLib.downloadFile(folder, downloadUrl, fileName)
            } else {
                return DownloadResult(DownloadResultType.FAILED, "Didn't find JSON", null)
            }
            return DownloadResult(DownloadResultType.SUCCESS, null, null)
        } catch (e: Exception) {
            return DownloadResult(DownloadResultType.FAILED, e.message, null)
        }
    }

    private fun cleanUpName(name: String): String =
        name.replace(Regex(CHARACTER_FILTER_REGEX), "").split(" ")[0]

    private fun isSnapshotVersion(json: JsonObject): Boolean =
        json["game_versions"].asJsonArray.none {
            val version = it.asString
            !version.contains(Regex("[a-zA-Z]"))
        }

    private fun isCompatibleWithServerType(json: JsonObject, serverType: ServerType): Boolean =
        if (serverType == ServerType.CUSTOM) true else json["loaders"].asJsonArray.map { it.asString }.contains(serverType.loaderName)

}