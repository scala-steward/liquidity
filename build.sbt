lazy val commonSettings = Seq(
  scalaVersion := "2.12.2",
  crossScalaVersions := Seq("2.11.11", "2.12.2"),
  scalacOptions in Compile ++= Seq(
    "-target:jvm-1.8",
    "-encoding",
    "UTF-8",
    "-deprecation",
    "-feature",
    "-unchecked",
    "-Xfuture",
    "-Xlint",
    "-Yno-adapted-args",
    "-Ywarn-dead-code",
    "-Ywarn-inaccessible",
    "-Ywarn-infer-any",
    "-Ywarn-nullary-override",
    "-Ywarn-nullary-unit",
    "-Ywarn-numeric-widen",
    "-Ywarn-unused",
    "-Ywarn-unused-import"
  ),
  resolvers += Resolver.bintrayRepo("dhpcs", "maven")
) ++
  addCommandAlias("validate", ";scalafmtTest; coverage; test; multi-jvm:test; coverageReport") ++
  addCommandAlias("validateAggregate", ";coverageAggregate")

lazy val publishSettings = Seq(
  organization := "com.dhpcs"
)

lazy val noopPublishSettings = Seq(
  publishM2 := {}
)

lazy val playJson = "com.typesafe.play" %% "play-json" % "2.6.0-M7"

lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.2"

lazy val model = project
  .in(file("model"))
  .settings(commonSettings)
  .settings(publishSettings)
  .settings(
    name := "liquidity-model"
  )
  .settings(
    libraryDependencies ++= Seq(
      playJson,
      "com.squareup.okio" % "okio" % "1.12.0"
    ))
  .settings(libraryDependencies += scalaTest % Test)

lazy val serialization = project
  .in(file("serialization"))
  .settings(commonSettings)
  .settings(noopPublishSettings)
  .settings(
    name := "liquidity-serialization"
  )
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % "2.4.17",
      playJson
    ))

lazy val persistence = project
  .in(file("persistence"))
  .settings(commonSettings)
  .settings(noopPublishSettings)
  .settings(
    name := "liquidity-persistence"
  )
  .settings(libraryDependencies +=
    "com.typesafe.akka" %% "akka-actor" % "2.4.17")
  .dependsOn(model)
  .dependsOn(serialization)

lazy val wsProtocol = project
  .in(file("ws-protocol"))
  .settings(commonSettings)
  .settings(publishSettings)
  .settings(
    name := "liquidity-ws-protocol"
  )
  .settings(libraryDependencies +=
    "com.dhpcs" %% "scala-json-rpc" % "2.0-M4")
  .dependsOn(model)
  .settings(libraryDependencies += scalaTest % Test)

lazy val actorProtocol = project
  .in(file("actor-protocol"))
  .settings(commonSettings)
  .settings(noopPublishSettings)
  .settings(
    name := "liquidity-actor-protocol"
  )
  .dependsOn(model)
  .dependsOn(wsProtocol)
  .dependsOn(serialization)

lazy val certgen = project
  .in(file("certgen"))
  .settings(commonSettings)
  .settings(noopPublishSettings)
  .settings(
    name := "liquidity-certgen"
  )
  .settings(libraryDependencies +=
    "org.bouncycastle" % "bcpkix-jdk15on" % "1.56")
  .settings(libraryDependencies += scalaTest % Test)

lazy val server = project
  .in(file("server"))
  .settings(commonSettings)
  .settings(noopPublishSettings)
  .settings(
    name := "liquidity-server"
  )
  .dependsOn(model)
  .dependsOn(persistence)
  .dependsOn(wsProtocol)
  .dependsOn(actorProtocol)
  .settings(libraryDependencies ++= Seq(
    "com.typesafe.akka"        %% "akka-slf4j"                          % "2.4.17",
    "ch.qos.logback"           % "logback-classic"                      % "1.2.3",
    "com.typesafe.akka"        %% "akka-actor"                          % "2.4.17",
    "com.typesafe.akka"        %% "akka-cluster"                        % "2.4.17",
    "com.typesafe.akka"        %% "akka-cluster-sharding"               % "2.4.17",
    "com.typesafe.akka"        %% "akka-cluster-tools"                  % "2.4.17",
    "com.typesafe.akka"        %% "akka-distributed-data-experimental"  % "2.4.17",
    "com.typesafe.akka"        %% "akka-persistence"                    % "2.4.17",
    "com.typesafe.akka"        %% "akka-persistence-query-experimental" % "2.4.17",
    "com.typesafe.akka"        %% "akka-persistence-cassandra"          % "0.26",
    "io.netty"                 % "netty-transport-native-epoll"         % "4.1.9.Final" classifier "linux-x86_64",
    "com.google.code.findbugs" % "jsr305"                               % "3.0.2" % Compile,
    "com.datastax.cassandra"   % "cassandra-driver-core"                % "3.2.0",
    "com.typesafe.akka"        %% "akka-http"                           % "10.0.5",
    playJson
  ))
  .dependsOn(certgen % "test")
  .settings(libraryDependencies ++= Seq(
    scalaTest           % Test,
    "com.typesafe.akka" %% "akka-http-testkit" % "10.0.5" % Test
  ))
  .configs(MultiJvm)
  .settings(SbtMultiJvm.multiJvmSettings)
  .dependsOn(certgen % MultiJvm)
  .settings(libraryDependencies ++= Seq(
    scalaTest              % MultiJvm,
    "com.typesafe.akka"    %% "akka-multi-node-testkit" % "2.4.17" % MultiJvm,
    "org.apache.cassandra" % "cassandra-all" % "3.10" % MultiJvm exclude ("io.netty", "netty-all")
  ))
  .enablePlugins(JavaAppPackaging, DockerPlugin)
  .settings(
    dockerBaseImage := "openjdk:8-jre",
    daemonUser in Docker := "root",
    bashScriptExtraDefines ++= Seq(
      "addJava -Djdk.tls.ephemeralDHKeySize=2048",
      "addJava -Djdk.tls.rejectClientInitiatedRenegotiation=true"
    )
  )

lazy val client = project
  .in(file("client"))
  .settings(commonSettings)
  .settings(publishSettings)
  .settings(
    name := "liquidity-client"
  )
  .dependsOn(model)
  .dependsOn(wsProtocol)
  .settings(libraryDependencies ++= Seq(
    "com.madgag.spongycastle" % "pkix"      % "1.54.0.0",
    "com.squareup.okhttp3"    % "okhttp-ws" % "3.4.2"
  ))
  .dependsOn(certgen % "test")
  .dependsOn(server % "test->test")
  .settings(libraryDependencies += scalaTest % Test)

lazy val healthcheck = project
  .in(file("healthcheck"))
  .settings(commonSettings)
  .settings(noopPublishSettings)
  .settings(
    name := "liquidity-healthcheck"
  )
  .dependsOn(client)
  .dependsOn(server % "compile->test")
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-stream-testkit" % "2.4.17",
      scalaTest
    ))
  .enablePlugins(JavaAppPackaging, UniversalPlugin)

lazy val boardgame = project
  .in(file("boardgame"))
  .settings(commonSettings)
  .settings(publishSettings)
  .settings(
    name := "liquidity-boardgame"
  )
  .dependsOn(client)

lazy val root = project
  .in(file("."))
  .settings(commonSettings)
  .settings(noopPublishSettings)
  .settings(
    name := "liquidity"
  )
  .aggregate(
    model,
    serialization,
    persistence,
    wsProtocol,
    actorProtocol,
    certgen,
    server,
    client,
    healthcheck,
    boardgame
  )
