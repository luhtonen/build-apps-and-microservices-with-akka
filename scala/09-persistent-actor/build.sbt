import Dependencies._

ThisBuild / scalaVersion     := "2.13.1"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "org.elu"
ThisBuild / organizationName := "elu"
val akkaVersion = "2.6.4"

lazy val root = (project in file("."))
  .settings(
      name := "09-persistent-actor",
      libraryDependencies += scalaTest % Test,
      libraryDependencies += "com.typesafe.akka" %% "akka-actor" % akkaVersion,
      libraryDependencies += "com.typesafe.akka" %% "akka-persistence" % akkaVersion,
      libraryDependencies += "com.typesafe.akka" %% "akka-serialization-jackson" % akkaVersion,
      libraryDependencies += "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8"
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
