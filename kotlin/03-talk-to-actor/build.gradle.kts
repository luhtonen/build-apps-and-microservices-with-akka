plugins {
  kotlin("jvm") version "1.3.61"
  application
}

dependencies {
  implementation(kotlin("stdlib"))
  implementation("com.typesafe.akka:akka-actor_2.13:2.6.3")
}

repositories {
  mavenCentral()
}

application {
  // Define the main class for the application.
  mainClassName = "org.elu.kotlin.akka.TalkToActorKt"
}
