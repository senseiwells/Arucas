plugins {
    kotlin("jvm") version "1.7.10"

    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("me.champeau.jmh") version "0.6.6"

    `maven-publish`
    application
}

group = "me.senseiwells"
version = "2.0.1"

val shade: Configuration by configurations.creating

repositories {
    mavenCentral()
}

dependencies {
    // This is super shady... (no pun intended)
    // implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")?.let { shade(it) }
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.7.10")?.let { shade(it) }
    implementation("com.google.code.gson:gson:2.9.0")?.let { shade(it) }

    testImplementation(kotlin("test"))
    testImplementation("org.openjdk.jmh:jmh-core:1.35")
    testAnnotationProcessor("org.openjdk.jmh:jmh-generator-annprocess:1.35")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    archiveClassifier.set("default")
}

tasks.shadowJar {
    configurations = listOf(shade)

    from("LICENSE")

    relocate("com.google", "shadow.google")
    relocate("org.jetbrains", "shadow.jetbrains")
    relocate("kotlinx", "shadow.kotlinx")
    relocate("kotlin", "shadow.kotlin")

    // @see https://youtrack.jetbrains.com/issue/KT-25709
    exclude("**/*.kotlin_metadata")
    exclude("**/*.kotlin_builtins")

    val name: String? = null
    archiveClassifier.set(name)

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
    mainClass.set("me.senseiwells.arucas.MainKt")
}

publishing {
    publications {
        register("mavenJava", MavenPublication::class) {
            artifact(tasks["shadowJar"])
            artifact(tasks["jar"])
        }
    }
}