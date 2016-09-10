lazy val commonSettings = Seq(
  scalaVersion := "2.11.8",
  organization := "com.dhpcs"
)

lazy val noopPublish = Seq(
  publishArtifact := false,
  publish := {},
  publishLocal := {}
)

lazy val playJsonRpc = "com.dhpcs" %% "play-json-rpc" % "1.1.1"

lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.0"

lazy val liquidityProtocol = project.in(file("protocol"))
  .settings(commonSettings)
  .settings(
    name := "liquidity-protocol",
    libraryDependencies ++= Seq(
      "com.squareup.okio" % "okio" % "1.10.0",
      playJsonRpc,
      scalaTest,
      playJsonRpc % "test->test"
    )
  )

lazy val liquidityServer = project.in(file("server"))
  .settings(commonSettings)
  .settings(noopPublish)
  .settings(
    name := "liquidity-server",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-slf4j" % "2.4.10",
      "ch.qos.logback" % "logback-classic" % "1.1.7",
      "com.typesafe.akka" %% "akka-http-experimental" % "2.4.10",
      "com.typesafe.play" %% "play-json" % "2.4.8" force(),
      "com.typesafe.akka" %% "akka-cluster-sharding" % "2.4.10",
      "com.typesafe.akka" %% "akka-persistence-cassandra" % "0.18",
      "com.typesafe.akka" %% "akka-persistence-query-experimental" % "2.4.10",
      scalaTest % "test",
      "com.typesafe.akka" %% "akka-http-testkit" % "2.4.10" % "test",
      "org.iq80.leveldb" % "leveldb" % "0.9" % "test"
    ),
    dockerBaseImage := "openjdk:8-jre",
    dockerExposedPorts := Seq(443),
    daemonUser in Docker := "root",
    bashScriptExtraDefines += "addJava -Djdk.tls.ephemeralDHKeySize=2048"
  )
  .dependsOn(liquidityProtocol)
  .dependsOn(liquidityCertgen % "test")
  .enablePlugins(JavaAppPackaging, DockerPlugin)

lazy val liquidityCertgen = project.in(file("certgen"))
  .settings(commonSettings)
  .settings(noopPublish)
  .settings(
    name := "liquidity-certgen",
    libraryDependencies ++= Seq(
      "org.bouncycastle" % "bcpkix-jdk15on" % "1.55"
    )
  )

lazy val liquidityBoardgame = project.in(file("boardgame"))
  .settings(commonSettings)
  .settings(
    name := "liquidity-boardgame",
    libraryDependencies ++= Seq(
      "com.madgag.spongycastle" % "pkix" % "1.54.0.0",
      "com.squareup.okhttp3" % "okhttp-ws" % "3.4.1"
    )
  )
  .dependsOn(liquidityProtocol)

lazy val liquidityAnalytics = project.in(file("analytics"))
  .settings(commonSettings)
  .settings(noopPublish)
  .settings(
    name := "liquidity-analytics"
  )
  .dependsOn(liquidityServer)

lazy val root = project.in(file("."))
  .settings(commonSettings)
  .settings(noopPublish)
  .settings(
    name := "liquidity-root"
  )
  .aggregate(
    liquidityProtocol,
    liquidityServer,
    liquidityCertgen,
    liquidityBoardgame,
    liquidityAnalytics
  )
