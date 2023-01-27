organization := "co.fs2"
name := "fs2-chat"

scalaVersion := "3.2.1"

libraryDependencies ++= Seq(
  "co.fs2" %% "fs2-io" % "3.5.0",
  "co.fs2" %% "fs2-scodec" % "3.5.0",
  "org.slf4j" % "slf4j-simple" % "1.7.30",
  "org.jline" % "jline" % "3.22.0",
  "org.typelevel" %% "cats-effect" % "3.4.5",
  "com.monovore" %% "decline" % "2.4.1",
  "com.monovore" %% "decline-effect" % "2.4.1"
  //, "dev.zio" %% "zio" % "1.0.14"

)

run / fork := true
outputStrategy := Some(StdoutOutput)
run / connectInput := true

scalafmtOnCompile := true

enablePlugins(UniversalPlugin, JavaAppPackaging)
