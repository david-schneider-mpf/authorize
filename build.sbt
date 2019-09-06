name := "authorize"

version := "0.1"

scalaVersion := "2.13.0"

libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.1.9"
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.5.24"

enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)
enablePlugins(AshScriptPlugin)

mainClass in Compile := Some("Main")

dockerBaseImage := "openjdk:jre-alpine"

dockerExposedPorts in Docker := Seq(8080)
dockerUsername in Docker := Option("davidschneidermpf")