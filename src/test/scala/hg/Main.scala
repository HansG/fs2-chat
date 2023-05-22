package hg

import cats.effect.{ExitCode, IO, IOApp}

object Main extends IOApp.Simple:
  def run: IO[Unit] =
    RunServer.stream[IO].compile.drain.as(ExitCode.Success)

