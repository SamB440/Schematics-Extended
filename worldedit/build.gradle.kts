plugins {
    id("java")
    id("com.github.johnrengelman.shadow")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

group = "com.convallyria"
version = "2.0.7"

repositories {
    mavenCentral()
    mavenLocal()

    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://maven.enginehub.org/repo/")
    maven("https://repo.lucko.me/")
    maven("https://repo.codemc.io/repository/nms/")
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")

    implementation("me.lucko:helper:5.6.13")

    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.3.0-SNAPSHOT") {
        exclude("com.google")
        exclude("org.bukkit")
        exclude("org.spigotmc")
    }

    compileOnly("org.jetbrains:annotations:23.0.0")
    compileOnly("org.spigotmc:spigot:1.19.2-R0.1-SNAPSHOT") // Included in codemc nms repo
}

tasks {
    build {
        dependsOn(shadowJar)
    }

    shadowJar {
        relocate("me.lucko.helper", "com.convallyria.schematics.extended.worldedit.lib.helper")
    }

    test {
        useJUnitPlatform()
    }
}