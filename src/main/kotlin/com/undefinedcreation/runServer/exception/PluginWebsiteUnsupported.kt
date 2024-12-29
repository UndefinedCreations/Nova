package com.undefinedcreation.runServer.exception

class PluginWebsiteUnsupported(url: String) : Exception("The website [$url] is unsupported!")