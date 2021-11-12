plugins {
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("java")
}

group = "com.convallyria"
version = "2.0.5"

java.sourceCompatibility = JavaVersion.VERSION_16
java.targetCompatibility = JavaVersion.VERSION_16

repositories {
    mavenCentral()
    mavenLocal()

    maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
    maven { url = uri("https://oss.sonatype.org/content/groups/public/") }
    maven { url = uri("https://repo.codemc.io/repository/nms/") }
    maven { url = uri("https://repo.lucko.me/") }
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")

    implementation("me.lucko:helper:5.6.8")

    compileOnly("org.spigotmc:spigot:1.17.1-R0.1-SNAPSHOT") // Included in codemc nms repo
    compileOnly("org.jetbrains:annotations:22.0.0")
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