package com.undefinedcreations.nova.exception

/**
 * Exception thrown when plugin download URL is not supported.
 */
class UnsupportedPluginPlatform(url: String) : Exception("The website [$url] is unsupported!")