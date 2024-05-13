apply(
    from = "https://github.com/SettingDust/FabricKotlinTemplate/raw/main/common.settings.gradle.kts"
)

val minecraft = settings.extra["minecraft"]
val kotlin = settings.extra["kotlin"]

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven2.bai.lol")
        maven("https://maven.fabricmc.net/") { name = "Fabric" }
        maven("https://maven.quiltmc.org/repository/release") { name = "Quilt" }
        maven("https://maven.minecraftforge.net/") { name = "Forge" }
        maven("https://repo.spongepowered.org/repository/maven-public/") { name = "Sponge" }
    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "org.spongepowered.mixin") {
                useModule("org.spongepowered:mixingradle:${requested.version}")
            }
        }
    }
}

dependencyResolutionManagement.versionCatalogs.named("catalog") {
    // https://modrinth.com/mod/blabber/versions
    library("blabber", "org.ladysnake", "blabber").version("1.4.0-mc$minecraft")
    // https://maven.ladysnake.org/#/releases/dev/onyxstudios/cardinal-components-api
    val cardinalComponents = "5.2.2"
    library(
            "cardinal-components-base",
            "dev.onyxstudios.cardinal-components-api",
            "cardinal-components-base"
        )
        .version(cardinalComponents)
    library(
            "cardinal-components-entity",
            "dev.onyxstudios.cardinal-components-api",
            "cardinal-components-entity"
        )
        .version(cardinalComponents)

    library("fabric-permissions-api", "me.lucko", "fabric-permissions-api").version("0.2-SNAPSHOT")

    // https://modrinth.com/mod/heracles/versions
    val heracles = "1.1.12"
    library("heracles-fabric", "maven.modrinth", "heracles").version("$heracles-fabric")
    library("heracles-forge", "maven.modrinth", "heracles").version("$heracles-forge")

    // https://modrinth.com/mod/resourceful-lib/versions
    val resourcefullib = "2.1.20"
    library(
            "resourceful-lib-fabric",
            "com.teamresourceful.resourcefullib",
            "resourcefullib-fabric-$minecraft"
        )
        .version(resourcefullib)
    library(
            "resourceful-lib-forge",
            "com.teamresourceful.resourcefullib",
            "resourcefullib-forge-$minecraft"
        )
        .version(resourcefullib)

    // https://modrinth.com/mod/kinecraft-serialization/versions
    library("kinecraft-serialization", "maven.modrinth", "kinecraft-serialization")
        .version("1.3.0-fabric")

    // https://modrinth.com/mod/heracles-for-villagers/versions
    library("heracles-villagers", "maven.modrinth", "heracles-for-villagers").version("0.3.1")

    // https://modrinth.com/mod/guard-villagers-(fabricquilt)/versions
    library("guard-villagers", "maven.modrinth", "guard-villagers-(fabricquilt)")
        .version("2.0.8-$minecraft")

    plugin("explosion", "lol.bai.explosion").version("0.1.0")
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
    // https://github.com/DanySK/gradle-pre-commit-git-hooks
    id("org.danilopianini.gradle-pre-commit-git-hooks") version "2.0.5"
}

gitHooks {
    preCommit {
        from {
            //             git diff --cached --name-only --diff-filter=ACMR | while read -r a; do
            // echo ${'$'}(readlink -f ${"$"}a); ./gradlew spotlessApply -q
            // -PspotlessIdeHook="${'$'}(readlink -f ${"$"}a)" </dev/null; done
            """
            export JAVA_HOME="${System.getProperty("java.home")}"
            ./gradlew spotlessApply spotlessCheck
            """
                .trimIndent()
        }
    }
    commitMsg { conventionalCommits { defaultTypes() } }
    hook("post-commit") {
        from {
            """
            files="${'$'}(git show --pretty= --name-only | tr '\n' ' ')"
            git add ${'$'}files
            git -c core.hooksPath= commit --amend -C HEAD
            """
                .trimIndent()
        }
    }
    createHooks(true)
}

val name: String by settings

rootProject.name = name

include("mod")

include("quilt")

include("forge")
