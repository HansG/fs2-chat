import org.checkerframework.checker.units.qual.{C, m2}

organization := "co.fs2"
name := "fs2-chat"

scalaVersion := "3.3.1"
val Http4sVersion = "0.23.18"

resolvers += "mvnrepository" at "https://mvnrepository.com/artifact"
//resolvers += "Local Maven Repository" at "file://D:/se/m2/repository"
resolvers += "Local Maven Repository" at "file://" + "c:/se/m2/repository"// Path.userHome.absolutePath
externalResolvers += "Local Maven Repository" at "file://" + "c:/se/m2/repository"// Path.userHome.absolutePath


libraryDependencies ++= Seq(
  "co.fs2" %% "fs2-io" % "3.7.0",
  "co.fs2" %% "fs2-scodec" % "3.7.0",
  "org.slf4j" % "slf4j-simple" % "2.0.5",
  "org.jline" % "jline" % "3.23.0",
  "org.typelevel" %% "cats-effect" % "3.5.0",
  "com.monovore" %% "decline" % "2.4.1",
  "com.monovore" %% "decline-effect" % "2.4.1",
  "com.monovore" %% "decline-refined" % "2.4.1",
  "com.softwaremill.sttp.openai" %% "core" % "0.1.1",
  "com.softwaremill.sttp.tapir" %% "tapir-core" % "1.10.6",
  "com.augustnagro" %% "magnum" % "1.1.1",
  "org.virtuslab" %% "besom-cfg" % "0.1.0",
  // https://mvnrepository.com/artifact/com.vladsch.flexmark/flexmark-all
  "com.vladsch.flexmark" %% "flexmark-all" % "0.64.8"
  //, "dev.zio" %% "zio" % "1.0.14"

)

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-ember-server" % "0.23.18" % Test,
  "org.http4s" %% "http4s-ember-client" % Http4sVersion % Test,
  "org.http4s" %% "http4s-circe" % Http4sVersion % Test,
  "org.http4s" %% "http4s-dsl" % Http4sVersion % Test,
  "org.scalameta" %% "munit" % "0.7.29" % Test,
  "org.typelevel" %% "munit-cats-effect-3" % "1.0.7" % Test,
  "ch.qos.logback" % "logback-classic" % "1.4.6" % Test,
  "com.softwaremill.sttp.client4" %% "core" % "4.0.0-M8" % Test
)

run / fork := true
outputStrategy := Some(StdoutOutput)
run / connectInput := true

scalafmtOnCompile := true

enablePlugins(UniversalPlugin, JavaAppPackaging)
