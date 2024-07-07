package com.undefinedcreation.runServer.lib

import com.undefinedcreation.runServer.RunServerTask
import org.gradle.api.Project
import org.gradle.api.Task

object TaskLib {
    fun getPluginTask(project: Project): Task {
        project.tasks.forEach {
            if (it.name == "shadowJar") {
                return it
            }
        }
        return project.tasks.named("jar").get()
    }
}