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
        name = "Jitpack"
        url = uri("https://jitpack.io")
    }
    flatDir {
        dirs("F:\\Documents\\Mod Libraries\\gc\\build\\libs")
    }
    flatDir {
        dirs("F:\\Documents\\Mod Libraries\\fc\\build\\libs")
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

    //reach attribute lib
    val reachVersion: String by project
    modImplementation("com.jamieswhiteshirt:reach-entity-attributes:$reachVersion"){
        exclude("net.fabricmc.fabric-api")
    }
    include("com.jamieswhiteshirt:reach-entity-attributes:$reachVersion")

    val meVersion: String by project
    implementation("com.github.llamalad7.mixinextras:mixinextras-fabric:$meVersion")
    annotationProcessor("com.github.llamalad7.mixinextras:mixinextras-fabric:$meVersion")
    
    val emiVersion: String by project
    modImplementation("dev.emi:emi:$emiVersion")

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
        sourceCompatibility = javaVersion.toString()
        targetCompatibility = javaVersion.toString()
    }
    jar { from("LICENSE") { rename { "${it}_${base.archivesName}" } } }
    processResources {
        inputs.property("version", project.version)
        filesMatching("fabric.mod.json") { expand(mutableMapOf("version" to project.version)) }
    }
    java {
        toolchain { languageVersion.set(JavaLanguageVersion.of(javaVersion.toString())) }
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
        withSourcesJar()
    }
}
