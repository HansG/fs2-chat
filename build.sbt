import org.checkerframework.checker.units.qual.{C, m2}

organization := "co.fs2"
name := "fs2-chat"

scalaVersion := "3.3.1"
val Http4sVersion = "0.23.27"

resolvers += "mvnrepository" at "https://mvnrepository.com/artifact"
//§§ "Local .. funktioniert! dependencies können über mvn geladen und über sbt gezogen werden -> Beachte: Syntax % statt %%
resolvers += "Local Maven Repository" at "file://" + "c:/se/m2/repository"// Path.userHome.absolutePath
externalResolvers += "Local Maven Repository" at "file://" + "c:/se/m2/repository"// Path.userHome.absolutePath


libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-simple" % "2.0.13",
  "org.jline" % "jline" % "3.23.0",
  "com.monovore" %% "decline" % "2.4.1",
  "com.monovore" %% "decline-effect" % "2.4.1",
  "com.monovore" %% "decline-refined" % "2.4.1",
  "org.typelevel" %% "cats-effect" % "3.5.0",
  "co.fs2" %% "fs2-io" % "3.10.2",
  "co.fs2" %% "fs2-scodec" % "3.10.2",
  "io.circe" %% "circe-generic" % "0.14.1",
  "com.softwaremill.sttp.openai" %% "core" % "0.2.0",
  "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % "1.10.7",
  "com.softwaremill.sttp.tapir" %% "tapir-core" % "1.10.7",
  "com.softwaremill.sttp.tapir" %% "tapir-sttp-client" % "1.10.7",
  "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % "1.10.8",
  "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % "1.10.7",
  "com.softwaremill.sttp.client4" %% "core" % "4.0.0-M14",
  "org.http4s" %% "http4s-ember-server" % "0.23.27",
  "org.http4s" %% "http4s-ember-client" % Http4sVersion,
  "org.http4s" %% "http4s-circe" % Http4sVersion,
  "org.http4s" %% "http4s-dsl" % Http4sVersion,
  "org.virtuslab" %% "besom-cfg" % "0.1.0",
  "org.scalameta" %% "munit" % "1.0.0",
  "com.augustnagro" %% "magnum" % "1.1.1",
  "ch.qos.logback" % "logback-classic" % "1.5.6",
  // https://mvnrepository.com/artifact/com.vladsch.flexmark/flexmark-all
  "com.vladsch.flexmark" % "flexmark-all" % "0.64.8" //§§ Unterschied %% <-> % : %% hängt _3 an, d.h. flexmark-all_3 statt flexmark-all
  //, "dev.zio" %% "zio" % "1.0.14"

)

/*libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-ember-server" % "0.23.27" % Test,
  "org.http4s" %% "http4s-ember-client" % Http4sVersion % Test,
  "org.http4s" %% "http4s-circe" % Http4sVersion % Test,
  "org.http4s" %% "http4s-dsl" % Http4sVersion % Test,
  "org.scalameta" %% "munit" % "1.0.0" % Test,
  "ch.qos.logback" % "logback-classic" % "1.5.6" % Test,
  "com.softwaremill.sttp.client4" %% "core" % "4.0.0-M8" % Test
)*/

run / fork := true
outputStrategy := Some(StdoutOutput)
run / connectInput := true

scalafmtOnCompile := true

enablePlugins(UniversalPlugin, JavaAppPackaging)
