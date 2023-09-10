import cuchaz.enigma.command.ConvertMappingsCommand

plugins {
    java
}

tasks {
    fun createTasks(): List<TaskProvider<out Task>> {
        val buildMappings = register("buildMappings") {
            file("build/mappings/mappings.tiny").delete()
            outputs.file("build/mappings/mappings.tiny")
            doLast {
                ConvertMappingsCommand.run(
                    "enigma",
                    project.file("mappings").toPath(),
                    "tinyv2:official:named",
                    project.file("build/mappings/mappings.tiny").toPath()
                )
            }
        }
        val lieToLoom = register("buildLoomMappings") {
            file("build/mappings/loom.tiny").delete()
            outputs.file("build/mappings/loom.tiny")
            doLast {
                ConvertMappingsCommand.run(
                    "enigma",
                    project.file("mappings").toPath(),
                    "tinyv2:intermediary:named",
                    project.file("build/mappings/loom.tiny").toPath()
                )
            }
        }

        val mappingsJar = register<Jar>("mappingsJar") {
            group = "build"
            archiveVersion.set(project.version.toString())
            from(buildMappings) {
                into("mappings")
            }
        }

        val loomMappingsJar = register<Jar>("loomMappingsJar") {
            group = "build"
            archiveClassifier.set("loom")
            archiveVersion.set(project.version.toString())
            from(lieToLoom) {
                into("mappings")
                rename("loom.tiny", "mappings.tiny")
            }
        }

        return listOf(buildMappings, mappingsJar, lieToLoom, loomMappingsJar)
    }

    build.get().dependsOn(createTasks())
}
