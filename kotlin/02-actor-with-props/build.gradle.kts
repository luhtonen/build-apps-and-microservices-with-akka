plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin.
    kotlin("jvm") version "1.3.61"

    // Apply the application plugin to add support for building a CLI application.
    application
}

repositories {
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.typesafe.akka:akka-actor_2.13:2.6.3")
}

application {
    // Define the main class for the application.
    mainClassName = "org.elu.kotlin.akka.CreationKt"
}
