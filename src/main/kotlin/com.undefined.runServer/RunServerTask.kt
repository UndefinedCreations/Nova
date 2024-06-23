package com.undefined.runServer

import org.gradle.api.DefaultTask

class RunServerTask: DefaultTask() {

    init {
        outputs.upToDateWhen { false }
    }

}