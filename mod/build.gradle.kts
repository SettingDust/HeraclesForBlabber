import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(catalog.plugins.fabric.loom)

    alias(catalog.plugins.kotlin.jvm)
    alias(catalog.plugins.kotlin.plugin.serialization)

    alias(catalog.plugins.explosion)
}

val id: String by rootProject.properties
val name: String by rootProject.properties
val author: String by rootProject.properties
val description: String by rootProject.properties

archivesName = name

kotlin { jvmToolchain(17) }

loom {
    splitEnvironmentSourceSets()

    mods {
        register(id) {
            sourceSet(sourceSets["main"])
            sourceSet(sourceSets["client"])
        }
    }

    runs {
        configureEach { ideConfigGenerated(true) }
        named("client") { name("Fabric Client") }
        named("server") { name("Fabric Server") }
    }
}

dependencies {
    minecraft(catalog.minecraft)
    mappings(variantOf(catalog.yarn) { classifier("v2") })

    modImplementation(catalog.fabric.loader)
    modImplementation(catalog.fabric.api)
    modImplementation(catalog.fabric.kotlin)

    val modClientImplementation by configurations
    modClientImplementation(catalog.modmenu)

    modImplementation(catalog.blabber)
    modImplementation(catalog.cardinal.components.base)
    modImplementation(catalog.cardinal.components.entity)
    modRuntimeOnly(catalog.fabric.permissions.api) { exclude(module = "fabric-loader") }

    modImplementation(explosion.fabric { maven(catalog.heracles.fabric.get().toString()) }) {
        exclude(module = "RoughlyEnoughItems-fabric")
        exclude(module = "fabric-loader")
        exclude(module = "fabric-api")
    }
    modImplementation(catalog.resourceful.lib.fabric)

    modImplementation(include(catalog.kinecraft.serialization.get()) {})

    modImplementation(catalog.heracles.villagers)
    modImplementation(catalog.guard.villagers)
    modRuntimeOnly(explosion.fabric { maven(catalog.guard.villagers.get().toString()) })
}

java {
    // Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
    // if it is present.
    // If you remove this line, sources will not be generated.
    withSourcesJar()
}

val metadata =
    mapOf(
        "group" to group,
        "author" to author,
        "id" to id,
        "name" to name,
        "version" to version,
        "description" to description,
        "source" to "https://github.com/SettingDust/HeraclesForBlabber",
        "minecraft" to "~1.20",
        "fabric_loader" to ">=0.12",
        "fabric_kotlin" to ">=1.10",
        "modmenu" to "*",
    )

tasks {
    withType<ProcessResources> {
        inputs.properties(metadata)
        filesMatching(listOf("fabric.mod.json", "*.mixins.json")) { expand(metadata) }
    }

    jar { from("LICENSE") }

    ideaSyncTask { enabled = true }

    withType<KotlinCompile> { kotlinOptions { freeCompilerArgs += "-Xjvm-default=all" } }
}
