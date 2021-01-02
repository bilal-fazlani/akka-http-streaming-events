lazy val root = project
  .in(file("."))
  .settings(
    name := "streaming-sample",
    version := "0.1.0",
    scalaVersion := "2.13.4",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-stream-typed" % "2.6.10",
      "com.typesafe.akka" %% "akka-http" % "10.2.2",
      "io.bullet" %% "borer-core" % "1.6.2",
      "io.bullet" %% "borer-derivation" % "1.6.2",
      "io.bullet" %% "borer-compat-akka" % "1.6.2",
      "ch.qos.logback" % "logback-classic" % "1.2.3"
    )
  )
