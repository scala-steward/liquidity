addSbtPlugin("io.get-coursier" % "sbt-coursier" % "1.0.2")

scalafmtVersion := "1.4.0"
addSbtPlugin("com.lucidchart" % "sbt-scalafmt-coursier" % "1.15")

libraryDependencies += "com.thesamet.scalapb" %% "compilerplugin" % "0.7.1"
addSbtPlugin("com.thesamet" % "sbt-protoc" % "0.99.17")

addSbtPlugin("com.dwijnand" % "sbt-dynver" % "2.1.0")
addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.8.0")
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.1")
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.3")
addSbtPlugin("ch.epfl.scala" % "sbt-release-early" % "2.1.1")
