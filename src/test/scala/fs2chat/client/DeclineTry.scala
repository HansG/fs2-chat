//> using platform "jvm"
//> using scala "3.2.1"
//> using lib "com.monovore::decline:2.4.1"
//> using lib "org.typelevel::cats-effect:3.4.5"
//> using mainClass "fs2chat.client.DeclineTry"


package fs2chat.client

import cats.effect.{ExitCode, IO, IOApp, Sync}
import cats.implicits.*
//import com.comcast.ip4s.*
import com.monovore.decline.*

import scala.io.StdIn

/*https://mpkocher.github.io/2022/06/02/CLI-App-leveraging-ZIO-and-Decline-using-scala-cli/
*/
object DeclineTry extends IOApp :

  val nameOpt = Opts.option[String]("user", help = "User name mit -u", "u")

  val alphaOpt = Opts
    .option[Double]("alpha", help = "Double eingeben mit -a!", "a")
    .withDefault(1.23)
  val forceOpt = Opts.flag("fail", help = "trigger failure mit -fail").orFalse

  def putStrLn(msg: Any) = Sync[IO].blocking(println(msg))

  val versionOpt: Opts[IO[Unit]] = Opts
    .flag("version", "Flag für: Show version and Exit mit -v", "v", visibility = Visibility.Partial)
    .orFalse.map(v => putStrLn("versionOpt: " + v))

  val mainOpt: Opts[IO[Unit]] =
    (nameOpt, alphaOpt, forceOpt).mapN[IO[Unit]] {
      (name, alpha, force) =>
        if (force)
          IO.raiseError(
            new Exception(s"Manually FAIL triggered by $name! alpha=$alpha")
          )
        else putStrLn(s"Hello $name. Running with alpha=$alpha")
    }

  val runOpt = versionOpt orElse mainOpt
  val command: Command[IO[Unit]] = Command[IO[Unit]](
    name = "declined",
    header = "Testing decline+zio"
  )(versionOpt orElse mainOpt)

  def effect(arg: List[String]): IO[Unit] = {
    IO(StdIn.readLine).flatMap { line =>
      command.parse(line.split(" ")) match {
        case Left(help) => // a bit odd that --help returns here
          if (help.errors.isEmpty) putStrLn(help.show)
          else IO.raiseError(new Exception(s"${help.errors}"))
        case Right(value) => value.map(_ => ())
      }
    }
  }


  override def run(args: List[String]): IO[ExitCode] =
    effect(args).handleErrorWith { ex =>
      putStrLn(s"Error ${ex}").as(ExitCode.Error)
    }.replicateA(10).map(_ => ExitCode.Success)

