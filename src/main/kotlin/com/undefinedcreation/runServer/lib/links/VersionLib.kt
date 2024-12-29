package com.undefinedcreation.runServer.lib.links

import com.google.gson.JsonParser
import java.net.URI

object VersionLib {

    fun paper(): List<String> =
        paperRepoVersions("paper")

    fun velocity(): List<String> =
        paperRepoVersions("velocity")

    fun folia(): List<String> =
        paperRepoVersions("folia")

    fun waterfall(): List<String> =
        paperRepoVersions("waterfall")

    fun spigot(): List<String> {
        val url = URI(Repositories.UNDEFINEDCREATION_REPO)
        val text = url.toURL().readText()
        val regex = """spigot-\d+\.\d+(\.\d+)?\.jar""".toRegex()
        val files = regex.findAll(text).map { it.value.replace("spigot-", "").replace(".jar", "") }.toSet()
        return files.toList()
    }

    fun bungeecord(): List<String> =
        listOf("ALL_VERSIONS") // DownloadLib get the last version of Bungeecord


    fun purper(): List<String> {
        val url = URI(Repositories.PURPER_REPO)
        val versions = JsonParser.parseString(url.toURL().readText())
            .asJsonObject.getAsJsonArray("versions")
        return versions.map { it.asString }
    }

    fun pufferfish(): List<String> {
        val url = URI(Repositories.PUFFERFISH_REPO.replace("/job", "/api/json"))
        val versions = JsonParser.parseString(url.toURL().readText())
            .asJsonObject.getAsJsonArray("jobs").filter {
                it.asJsonObject["name"].asString.contains("Pufferfish-1")
            }.map { it.asJsonObject["name"].asString.split("-")[1] }
        return versions
    }


    private fun paperRepoVersions(projectName: String): List<String> {
        val url = URI("${Repositories.PAPERMC_REPO}/$projectName")
        val versions = JsonParser.parseString(url.toURL().readText())
            .asJsonObject.getAsJsonArray("versions")
        return versions.map { it.asString }
    }

}