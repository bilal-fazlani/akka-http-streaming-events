import sbt._

object Libs {
  lazy val `logback-classic` = "ch.qos.logback" % "logback-classic" % "1.2.3"
}

object Borer {
  private val org = "io.bullet"
  private val version = "1.6.2"
  lazy val `borer-derivation` = org %% "borer-derivation" % version
  lazy val `borer-compat-akka` = org %% "borer-compat-akka" % version
}

object Akka {
  private val org = "com.typesafe.akka"
  lazy val `akka-stream-typed` = org %% "akka-stream-typed" % "2.6.10"
  lazy val `akka-http` = org %% "akka-http" % "10.2.2"
}
