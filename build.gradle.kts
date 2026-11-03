plugins {
    id("java-library")
}

repositories {
    maven("https://hub.spigotmc.org/nexus/content/groups/public/")
    mavenCentral()
    mavenLocal() // This is needed for CraftBukkit and Spigot.
}

dependencies {
    // Pick only one of these and read the comment in the repositories block.
    compileOnly("org.spigotmc:spigot-api:1.14.4-R0.1-SNAPSHOT") // The Spigot API with no shadowing. Requires the OSS repo.
    compileOnly("org.xerial:sqlite-jdbc:3.27.2.1")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(8)
}

tasks {
    processResources {
        val props = mapOf("version" to version)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}
