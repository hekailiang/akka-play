name := "akka-io-play"

version := "1.0"

scalaVersion := "2.11.4"

libraryDependencies ++= {
  val akkaVersion = "2.3.6"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "ch.qos.logback" % "logback-classic" % "1.1.2",
    "org.scalatest" %% "scalatest" % "2.2.0"
  )
}