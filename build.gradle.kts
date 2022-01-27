plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("io.papermc.paperweight.userdev") version "1.3.3"
    id("java")
}

group = "com.convallyria"
version = "2.0.6"

repositories {
    mavenCentral()
    mavenLocal()

    maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
    maven { url = uri("https://oss.sonatype.org/content/groups/public/") }
    maven { url = uri("https://repo.codemc.io/repository/nms/") }
    maven { url = uri("https://repo.lucko.me/") }
}

dependencies {
    paperDevBundle("1.18.1-R0.1-SNAPSHOT")

    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")

    implementation("me.lucko:helper:5.6.8")
    compileOnly("org.jetbrains:annotations:22.0.0")
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