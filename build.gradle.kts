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

tasks {
    // Configure reobfJar to run when invoking the build task
    assemble {
        dependsOn(reobfJar)
    }

    shadowJar {
        relocate("me.lucko.helper", "com.convallyria.schematics.extended.lib.helper")
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything

        // Set the release flag. This configures what version bytecode the compiler will emit, as well as what JDK APIs are usable.
        // See https://openjdk.java.net/jeps/247 for more information.
        options.release.set(17)
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name() // We want UTF-8 for everything
        filesMatching("plugin.yml") {
            expand("version" to project.version)
        }
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}