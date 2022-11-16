plugins {
    id("java")
    id("com.github.johnrengelman.shadow")
    id("io.papermc.paperweight.userdev") version "1.3.10-SNAPSHOT"
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

group = "com.convallyria"
version = "2.0.7"

repositories {
    mavenCentral()
    mavenLocal()

    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://repo.codemc.io/repository/nms/")
    maven("https://repo.lucko.me/")
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")

    paperDevBundle("1.19.2-R0.1-SNAPSHOT")

    implementation("me.lucko:helper:5.6.13")
    compileOnly("org.jetbrains:annotations:23.0.0")
}

tasks {
    // Configure reobfJar to run when invoking the build task
    assemble {
        dependsOn(reobfJar)
    }

    build {
        dependsOn(shadowJar)
    }

    shadowJar {
        relocate("me.lucko.helper", "com.convallyria.schematics.extended.nms.lib.helper")
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name() // We want UTF-8 for everything
        filesMatching("plugin.yml") {
            expand("version" to project.version)
        }
    }

    test {
        useJUnitPlatform()
    }
}