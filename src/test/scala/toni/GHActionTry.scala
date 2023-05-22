package toni

import cats.effect.IO
import com.monovore.decline.Opts
import com.monovore.decline.effect.CommandIOApp
import cats.syntax.all.*
import fs2.io.file.{Files, Path}
import fs2.Stream
import cats.effect.{ExitCode, IO, IOApp}
import cats.effect.std.Env

//https://toniogela.dev/gh-action-in-scala/

object GHActionTry:
  // produce a .js file: scala-cli --power package -f GHAction.scala  -> GHAction.js
  // > using scala "3.2.2"
  //> using platform "js"
  //> using jsVersion "1.13.1"
  //> using jsModuleKind "common"
  //> using dep "org.typelevel::toolkit::latest.release"

//  import cats.effect.{ExitCode, IO, IOApp}  dazunehmen zum gen
//  import cats.effect.std.Env
  // import cats.effect.{IO, ExitCode}
  //  import fs2.Stream
  // import fs2.io.file.{Files, Path}
  //  import cats.syntax.all.*
  //  import com.monovore.decline.Opts
  //  import com.monovore.decline.effect.CommandIOApp

  def getInput(input: String): IO[Option[String]] =
    Env[IO].get(s"INPUT_${input.toUpperCase.replace(' ', '_')}")

  def outputFile: IO[Path] =
    Env[IO].get("GITHUB_OUTPUT").map(_.get).map(Path.apply) // unsafe Option.get

  def setOutput(name: String, value: String): IO[Unit] =
    outputFile.flatMap(path =>
      Files[IO].writeAll(path).apply(Stream.iterable(s"${name}=${value}".getBytes))
        //.through(Files[IO].writeAll(path))
        .compile
        .drain
    )

  object index extends IOApp.Simple:
    def run = for {
      number1 <- getInput("number-one").map(_.get.toInt) // unsafe Option.get
      number2 <- getInput("number-two").map(_.get.toInt) // unsafe Option.get
      _ <- setOutput("result", s"${number1 + number2}")
    } yield ()

end GHActionTry



object GHActionDTry:
  // produce a .js file: scala-cli --power package -f GHAction.scala  -> GHAction.js
  //> using scala "3.2.2"
  //> using platform "js"
  //> using jsVersion "1.13.1"
  //> using jsModuleKind "common"
  //> using dep "org.typelevel::toolkit::latest.release"


  val args = (
    Opts.env[Int]("INPUT_NUMBER-ONE", "The first number"),
    Opts.env[Int]("INPUT_NUMBER-TWO", "The second number"),
    Opts.env[String]("GITHUB_OUTPUT", "The file of the output").map(Path.apply)
  )

  object indexD extends CommandIOApp("adder", "Summing two numbers"):
    def main = args.mapN { (one, two, path) =>
      Files[IO].writeAll(path).apply(Stream.emits(s"result=${one + two}".getBytes))
//        .through(Files[IO].writeAll(path)) compiliert nicht!
        .compile
        .drain
        .as(ExitCode.Success)
    }

end GHActionDTry

object GHActionDTest extends CommandIOApp("adder", "Summing two numbers"):
  import GHActionDTry.*
  def main = args.mapN { (one, two, path) =>
    Files[IO].writeAll(path).apply(Stream.emits(s"result=${one + two}".getBytes))
      //        .through(Files[IO].writeAll(path)) compiliert nicht!
      .compile
      .drain
      .as(ExitCode.Success)
  }
