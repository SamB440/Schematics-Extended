plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("java")
}

group = "com.convallyria"
version = "2.0.7"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation(project(":nms", "reobf"))
    implementation(project(":worldedit", "shadow"))
}

tasks {
    build {
        dependsOn(shadowJar)
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

    shadowJar {
        archiveClassifier.set("")
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}