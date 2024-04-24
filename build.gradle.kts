plugins {
    id("fabric-loom")
    val kotlinVersion: String by System.getProperties()
    kotlin("jvm").version(kotlinVersion)
}
base {
    val archivesBaseName: String by project
    archivesName.set(archivesBaseName)
}
val modVersion: String by project
version = modVersion
val mavenGroup: String by project
group = mavenGroup
repositories {
    maven {
        name = "TerraformersMC"
        url = uri("https://maven.terraformersmc.com/")
    }
    maven {
        name = "REI"
        url = uri("https://maven.shedaniel.me")
    }
    maven {
        name = "Ladysnake Libs"
        url = uri("https://ladysnake.jfrog.io/artifactory/mods")
    }
    maven {
        url = uri("https://maven.jamieswhiteshirt.com/libs-release")
        content {
            includeGroup( "com.jamieswhiteshirt")
        }
    }
    maven {
        name = "Modrinth"
        url = uri("https://api.modrinth.com/maven")
        content {
            includeGroup("maven.modrinth")
        }
    }
    maven {
        name = "Jitpack"
        url = uri("https://jitpack.io")
    }
    flatDir {
        dirs("E:\\Documents\\Mod Libraries\\gc\\build\\libs")
    }
    flatDir {
        dirs("E:\\Documents\\Mod Libraries\\fc\\build\\libs")
    }
    flatDir {
        dirs("E:\\Documents\\Mod Development\\reach-entity-attributes\\build\\libs")
    }
}
dependencies {
    val minecraftVersion: String by project
    minecraft("com.mojang:minecraft:$minecraftVersion")
    val yarnMappings: String by project
    mappings("net.fabricmc:yarn:$yarnMappings:v2")
    val loaderVersion: String by project
    modImplementation("net.fabricmc:fabric-loader:$loaderVersion")
    val fabricVersion: String by project
    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricVersion")
    val fabricKotlinVersion: String by project
    modImplementation("net.fabricmc:fabric-language-kotlin:$fabricKotlinVersion")

    val reiVersion: String by project
    modCompileOnly("me.shedaniel:RoughlyEnoughItems-api:$reiVersion")
    modCompileOnly("me.shedaniel:RoughlyEnoughItems-api-fabric:$reiVersion")
    modRuntimeOnly("me.shedaniel:RoughlyEnoughItems-fabric:$reiVersion")

    val fcVersion: String by project
    modImplementation(":fzzy_core-$fcVersion"){
        exclude("net.fabricmc.fabric-api")
    }

    val gcVersion: String by project
    modImplementation(":gear_core-$gcVersion"){
        exclude("net.fabricmc.fabric-api")
    }

    //reach attribute lib, refactor again if 1.20 version officially released
    val reachVersion: String by project
    modImplementation(":reach-entity-attributes:$reachVersion"){
        exclude("net.fabricmc.fabric-api")
    }
    include(":reach-entity-attributes:$reachVersion")

    val meVersion: String by project
    implementation("com.github.llamalad7.mixinextras:mixinextras-fabric:$meVersion")
    annotationProcessor("com.github.llamalad7.mixinextras:mixinextras-fabric:$meVersion")

    val emiVersion: String by project
    modCompileOnly ("dev.emi:emi-fabric:${emiVersion}:api")
    modLocalRuntime ("dev.emi:emi-fabric:${emiVersion}")

    val fzzyConfigVersion: String by project
    modImplementation("maven.modrinth:fzzy-config:$fzzyConfigVersion")

}
tasks {
    val javaVersion = JavaVersion.VERSION_17
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        sourceCompatibility = javaVersion.toString()
        targetCompatibility = javaVersion.toString()
        options.release.set(javaVersion.toString().toInt())
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions { jvmTarget = javaVersion.toString() }
    }
    jar { from("LICENSE") { rename { "${base.archivesName.get()}_${it}" } } }
    processResources {
        val fcVersion: String by project
        val gcVersion: String by project
        val fabricKotlinVersion: String by project
        val archivesBaseName: String by project
        inputs.property("fcVersion", fcVersion)
        inputs.property("gcVersion", gcVersion)
        inputs.property("fabricKotlinVersion", fabricKotlinVersion)
        inputs.property("version", project.version)
        inputs.property("id", archivesBaseName)
        filesMatching("fabric.mod.json") {
            expand(
                mutableMapOf(
                    "version" to project.version,
                    "id" to archivesBaseName,
                    "fcVersion" to fcVersion,
                    "gcVersion" to gcVersion,
                    "fabricKotlinVersion" to fabricKotlinVersion))
        }
    }
    java {
        toolchain { languageVersion.set(JavaLanguageVersion.of(javaVersion.toString())) }
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
        withSourcesJar()
    }
}