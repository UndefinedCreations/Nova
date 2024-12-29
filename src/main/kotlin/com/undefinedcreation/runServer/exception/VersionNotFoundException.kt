package com.undefinedcreation.runServer.exception

class VersionNotFoundException(mcVersion: String, versions: List<String>): Exception(
    "$mcVersion wasn't found inside $versions"
)