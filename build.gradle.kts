plugins {
    kotlin("jvm") version "1.7.10"

    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("me.champeau.jmh") version "0.6.6"

    `maven-publish`
    application
}

group = "me.senseiwells"
version = "2.3.0"

val shade: Configuration by configurations.creating

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.7.10")?.let { shade(it) }
    implementation("com.google.code.gson:gson:2.9.0")?.let { shade(it) }
    implementation("net.bytebuddy:byte-buddy:1.12.23")?.let { shade(it) }

    testImplementation(kotlin("test"))
    testImplementation("org.openjdk.jmh:jmh-core:1.35")
    testAnnotationProcessor("org.openjdk.jmh:jmh-generator-annprocess:1.35")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    from("LICENSE")
}

tasks.shadowJar {
    configurations = listOf(shade)

    // @see https://youtrack.jetbrains.com/issue/KT-25709
    exclude("**/*.kotlin_metadata")
    exclude("**/*.kotlin_builtins")

    from("LICENSE")

    archiveClassifier.set("fat")

    // archiveFileName.set("${rootProject.name}-${archiveVersion.get()}.jar")
}

tasks.distTar {
    dependsOn("shadowJar")
}

tasks.distZip {
    dependsOn("shadowJar")
}

tasks.startScripts {
    dependsOn("shadowJar")
}

application {
    mainClass.set("me.senseiwells.arucas.Arucas")
}

publishing {
    publications {
        register("mavenJava", MavenPublication::class) {
            artifact(tasks["shadowJar"])
            artifact(tasks["jar"])
        }
    }
}