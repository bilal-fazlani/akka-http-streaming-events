inThisBuild(
  Seq(
    version := "0.1.0",
    scalaVersion := "2.13.4"
  )
)

lazy val root = project
  .in(file("."))
  .settings(
    name := "streaming-sample"
  )
  .aggregate(server, client)

lazy val server = project
  .in(file("./server"))
  .settings(
    name := "streaming-server",
    libraryDependencies ++= Seq(
      Libs.`logback-classic`,
      Akka.`akka-http`,
      Akka.`akka-stream-typed`,
      Borer.`borer-compat-akka`,
      Borer.`borer-derivation`
    )
  )

lazy val client = project
  .in(file("./client"))
  .settings(
    name := "streaming-client",
    libraryDependencies ++= Seq(
      Libs.`logback-classic`,
      Akka.`akka-http`,
      Akka.`akka-stream-typed`,
      Borer.`borer-compat-akka`,
      Borer.`borer-derivation`
    )
  )
