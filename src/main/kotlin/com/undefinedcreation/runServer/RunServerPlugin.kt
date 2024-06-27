package com.undefinedcreation.runServer

import com.undefinedcreation.runServer.lib.TaskLib
import org.gradle.api.Plugin
import org.gradle.api.Project

class RunServerPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        target.tasks.register("runServer", RunServerTask::class.java) {
            it.group = "undefined"
            it.description = "This task will run an minecraft server inside your Intellij"
        }.get().dependsOn(TaskLib.getPluginTask(target))
    }
}