name := "Raven-PiModule"

version := "1.0"

scalaVersion := "2.12.2"

mainClass in (Compile, run) := Some("Main")

libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-actor" % "2.5.1",
    "com.typesafe.akka" %% "akka-stream" % "2.5.1",
    "com.typesafe.akka" %% "akka-remote" % "2.5.1"
)
