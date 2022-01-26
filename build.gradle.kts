plugins {
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("java")
}

group = "com.convallyria"
version = "2.0.6"

repositories {
    mavenCentral()

    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://maven.enginehub.org/repo/")
    maven("https://repo.lucko.me/")
    maven("https://repo.codemc.io/repository/nms/")
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")

    implementation("me.lucko:helper:5.6.8")

    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.3.0-SNAPSHOT") {
        exclude("com.google")
        exclude("org.bukkit")
        exclude("org.spigotmc")
    }
    compileOnly("org.jetbrains:annotations:22.0.0")
    compileOnly("org.spigotmc:spigot:1.18.1-R0.1-SNAPSHOT") // Included in codemc nms repo
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.shadowJar {
    relocate("me.lucko.helper", "com.convallyria.schematics.extended.lib.helper")
}

tasks.processResources {
    filesMatching("plugin.yml") {
        expand("version" to project.version)
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}