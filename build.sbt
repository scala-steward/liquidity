name := """liquidity-common"""

organization := "com.dhpcs"

version := "0.9.0"

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-json" % "2.3.9",
  "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test",
  "com.google.guava" % "guava" % "18.0",
  "com.google.code.findbugs" % "jsr305" % "3.0.0"
)
