//> using platform "jvm"
//> using scala "3.2.1"
//> using lib "com.monovore::decline:2.4.1"
//> using lib "org.typelevel::cats-effect:3.4.5"
//> using mainClass "fs2chat.client.DeclineTry"


package fs2chat.client


import java.net.URI
import scala.concurrent.duration.Duration
import java.nio.file.Path
import cats.effect.*
import cats.implicits.*
import com.monovore.decline.*
import com.monovore.decline.effect.*
import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric.Positive
import com.monovore.decline.refined.*

import scala.io.StdIn


/*
https://ben.kirw.in/decline/

mit scala-cli laufen lassen:
https://mpkocher.github.io/2022/06/02/CLI-App-leveraging-ZIO-and-Decline-using-scala-cli/
siehe oben: //> using
scala-cli run DeclineCatsTry.scala -- build -f hallo.scala c:/se   -> Command Execute: BuildImage(Some(hallo.scala),c:/se)
scala-cli run DeclineCatsTry.scala -- build  c:/se                 -> Command Execute: BuildImage(None,c:/se)
oder
scala-cli package --jvm 17 DeclineCatsTry.scala
*/
object DeclineCatsTry :

  // We'll start by defining our individual options...
  val uriOpt = Opts.option[URI]("input-uri", "Location of the remote file.")
  // uriOpt: Opts[URI] = Opts(--input-uri <uri>)
  val uriOptV =
    Opts.option[URI]("uri", "Location of the remote file.")
      .validate("remote uri must be https")(_.getScheme == "https")
    // uriOpt: Opts[URI] = Opts(--uri <uri>)

  val timeoutOpt = Opts.option[Duration]("timeout", "Timeout for fetching the remote file.")
      .withDefault(Duration.Inf)
  // timeoutOpt: Opts[Duration] = Opts([--timeout <duration>])
  val fileOpt = Opts.option[Path]("input-file", "Local path to input file.")
  // fileOpt: Opts[Path] = Opts(--input-file <path>)
  val outputOpt = Opts.argument[Path]("output-file")
  // outputOpt: Opts[Path] = Opts(<output-file>)

  // ...along with a case class that captures all our configuration data.
  case class Config(
                     uri: Option[URI],
                     timeout: Duration,
                     file: Option[Path],
                     output: Path,
                   )

  // Then, we combine our individual options into a `Opts[Config]` and validate the result.
  val configOpts: Opts[Config] =
    (uriOpt.orNone, timeoutOpt, fileOpt.orNone, outputOpt)
      .mapN(Config.apply)
      .validate("remote uri must be https")(_.uri.forall(_.getScheme == "https"))
      .validate("timeout option is only valid for remote files")(c =>
        // if a non-default timeout is specified, uri must be present
        c.timeout != Duration.Inf || c.uri.isDefined
      )
      .validate("must provide either uri or file")(c => c.uri.isDefined ^ c.file.isDefined)
  // configOpts: Opts[Config] = Opts([--input-uri <uri>] [--timeout <duration>] [--input-file <path>] <output-file>)

  case class RemoteConfig(uri: URI, timeout: Duration)

  val remoteOpts = (uriOpt, timeoutOpt).mapN(RemoteConfig.apply)
  // remoteOpts: Opts[RemoteConfig] = Opts(--uri <uri> [--timeout <duration>])

  case class ConfigR(
                     remote: Option[RemoteConfig],
                     file: Option[Path],
                     output: Path,
                   )

  val configOptsR =
    (remoteOpts.orNone, fileOpt.orNone, outputOpt)
      .mapN(ConfigR.apply)
      .validate("must provide either uri or file")(c => c.remote.isDefined ^ c.file.isDefined)
    // configOpts: Opts[Config] = Opts([--uri <uri> [--timeout <duration>]] [--input-file <path>] <output-file>)




  // And finally, we pass the validated config to a `run` function that does the real work.
  def runApp(config: Config) = ???
  configOpts.map(runApp)
  // res0: Opts[Nothing] = Opts([--input-uri <uri>] [--timeout <duration>] [--input-file <path>] <output-file>)



class DeclineCatsTry1:
  val uriOpt =
    Opts.option[URI]("uri", "Location of the remote file.")
      .validate("remote uri must be https")(_.getScheme == "https")
  // uriOpt: Opts[URI] = Opts(--uri <uri>)

  val timeoutOpt = Opts.option[Duration]("timeout", "Timeout for fetching the remote file.")
    .withDefault(Duration.Inf)
  // timeoutOpt: Opts[Duration] = Opts([--timeout <duration>])
  val fileOpt = Opts.option[Path]("input-file", "Local path to input file.")
  // fileOpt: Opts[Path] = Opts(--input-file <path>)

  val outputOpt = Opts.argument[Path]("output-file")
  // outputOpt: Opts[Path] = Opts(<output-file>)



object DeclineCatsTry1a extends DeclineCatsTry1:
  // Either would for two mutually-exclusive possibilities,
  // but a sealed trait is a bit more general.
  sealed trait InputConfig
  case class RemoteConfig(uri: URI, timeout: Duration) extends InputConfig
  case class LocalConfig(file: Path) extends InputConfig

  val remoteOpts = (uriOpt, timeoutOpt).mapN(RemoteConfig.apply)
  // remoteOpts: Opts[RemoteConfig] = Opts(--uri <uri> [--timeout <duration>])
  val localOpts = fileOpt.map(LocalConfig.apply)
  // localOpts: Opts[LocalConfig] = Opts(--input-file <path>)
  val inputOpts = remoteOpts orElse localOpts
  // inputOpts: Opts[Product with Serializable with InputConfig] = Opts(--uri <uri> [--timeout <duration>] | --input-file <path>)

  case class Config(
                     input: InputConfig,
                     queries: Path,
                   )

  val configOpts = (inputOpts, outputOpt).mapN(Config.apply)
  // configOpts: Opts[Config] = Opts(--uri <uri> [--timeout <duration>] <output-file> | --input-file <path> <output-file>)


object DeclineCatsTry1b extends DeclineCatsTry1:
  // This example code uses cats-effect's IO, but the pattern works just as well for
  // imperative programs... just change the return type of the fetch functions
  // to `Future[String]`, `String`, or whatever else makes sense in your context.
  import cats.effect.IO

  def fetchRemote(uri: URI, timeout: Duration): IO[String] = ???
  def fetchLocal(file: Path): IO[String] = ???

  val remoteOpts = (uriOpt, timeoutOpt).mapN(fetchRemote)
  // remoteOpts: Opts[IO[String]] = Opts(--uri <uri> [--timeout <duration>])
  val localOpts = fileOpt.map(fetchLocal)
  // localOpts: Opts[IO[String]] = Opts(--input-file <path>)
  val inputOpts = remoteOpts orElse localOpts
  // inputOpts: Opts[IO[String]] = Opts(--uri <uri> [--timeout <duration>] | --input-file <path>)

  def run(input: IO[String], output: Path): IO[Unit] = ???

  val configOpts = (inputOpts, outputOpt).mapN(run)
  // configOpts: Opts[IO[Unit]] = Opts(--uri <uri> [--timeout <duration>] <output-file> | --input-file <path> <output-file>)

object DeclineDockerAppTry extends IOApp :
  case class ShowProcesses(all: Boolean)
  case class BuildImage(dockerFile: Option[String], path: String)

  val showProcessesOpts: Opts[ShowProcesses] =
    Opts.subcommand("ps", "Lists docker processes running!") {
      Opts.flag("all", "Whether to show all running processes.", short = "a")
        .orFalse
        .map(ShowProcesses.apply)
    }
  val dockerFileOpts: Opts[Option[String]] =
    Opts.option[String]( "file", "The name of the Dockerfile.", short = "f" ).orNone
  // dockerFileOpts: Opts[Option[String]] = Opts([--file <string>])

  val pathOpts: Opts[String] =
    Opts.argument[String](metavar = "path")
  // pathOpts: Opts[String] = Opts(<path>)

  val buildOpts: Opts[BuildImage] =
    Opts.subcommand("build", "Builds a docker image!") {
      (dockerFileOpts, pathOpts).mapN(BuildImage.apply)
    }

  val command = Command(name = "docker", header = "Faux docker command line" ) (showProcessesOpts orElse buildOpts)

  def effect(cmd: String ) = command.parse(cmd.split(" ")).map {
    case Left(help) => IO.raiseError(new Exception(s"${help}"))
    case Right(product) => product match {
      case ShowProcesses(all) => IO("Command Execute: ShowProcesses" + all)
      case BuildImage(dockerFile, path) => IO(s"Command Execute: BuildImage $dockerFile  $path")
    }
  }

  override def run(args: List[String]): IO[ExitCode] =
    IO(StdIn.readLine).flatMap { line =>
      effect(line).handleErrorWith { ex =>
        IO(println(s"Error ${ex}"))
      }.replicateA(10).map(_ => ExitCode.Success)
    }


object DeclineDockerTry extends CommandIOApp(
  name = "docker",
  header = "Faux docker command line",
  version = "0.0.x"
  ):
  case class ShowProcesses(all: Boolean)
  case class BuildImage(dockerFile: Option[String], path: String)

  override def main: Opts[IO[ExitCode]] =
    (showProcessesOpts orElse buildOpts).map {
      case c @ ShowProcesses(all) => IO(println("Command Execute: "+ c))
      case c @ BuildImage(dockerFile, path) => IO(println("Command Execute: "+ c))
    }.map ( _.as(ExitCode.Success) )


  val showProcessesOpts: Opts[ShowProcesses] =
    Opts.subcommand("ps", "Lists docker processes running!") {
      Opts.flag("all", "Whether to show all running processes.", short = "a")
        .orFalse
        .map(ShowProcesses.apply)
    }
  // showProcessesOpts: Opts[ShowProcesses] = Opts(ps)

  val dockerFileOpts: Opts[Option[String]] =
    Opts.option[String]( "file", "The name of the Dockerfile.", short = "f" ).orNone
  // dockerFileOpts: Opts[Option[String]] = Opts([--file <string>])

  val pathOpts: Opts[String] =
    Opts.argument[String](metavar = "path")
  // pathOpts: Opts[String] = Opts(<path>)

  val buildOpts: Opts[BuildImage] =
    Opts.subcommand("build", "Builds a docker image!") {
      (dockerFileOpts, pathOpts).mapN(BuildImage.apply)
    }
  // buildOpts: Opts[BuildImage] = Opts(build)



object DeclineRefinedTry extends App:
  type PosInt = Int Refined Positive

  val lines = Command("lines", "Parse a positive number of lines.") {
    Opts.argument[PosInt]("count")
  }

  println(lines.parse(Seq("10")))
  // res0: Either[Help, PosInt] = Right(10)
  println(lines.parse(Seq("0")))
  // res1: Either[Help, PosInt] = Left(Predicate failed: (0 > 0).







