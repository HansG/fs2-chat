package fs2chat.client

import cats.effect.{ExitCode, IO, IOApp, Sync}
import cats.implicits.*
import com.comcast.ip4s.*
import com.monovore.decline.*
import fs2chat.{Console, Protocol, UserQuit, Username}
import fs2.Stream
import fs2.interop.scodec.{StreamDecoder, StreamEncoder}

object ClientAppTry extends IOApp:
  private val argsParser: Command[(Username, SocketAddress[IpAddress])] =
    Command("fs2chat-client", "FS2 Chat Client") {
      (
        Opts
          .option[String]("username", "Desired username", "u")
          .map(Username.apply),
        Opts
          .option[String]("address", "Address of chat server")
          .withDefault("127.0.0.1")
          .mapValidated(p => IpAddress.fromString(p).toValidNel("Invalid IP address")),
        Opts
          .option[Int]("port", "Port of chat server")
          .withDefault(5555)
          .mapValidated(p => Port.fromInt(p).toValidNel("Invalid port number"))
      ).mapN { case (desiredUsername, ip, port) =>
        desiredUsername -> SocketAddress(ip, port)
      }
    }

  val nameOpt = Opts.option[String]("user", help = "User name", "u")

  val alphaOpt = Opts
    .option[Double]("alpha", help = "Alpha Filtering", "a")
    .withDefault(1.23)
  val forceOpt = Opts.flag("fail", help = "Manually trigger a failure").orFalse

  def printlnio(msg : Any) = Sync[IO].blocking(println(msg))

  val versionOpt: Opts[IO[Unit]] = Opts
    .flag("version", "Show version and Exit", "v", visibility = Visibility.Partial )
    .orFalse.map( printlnio(_))

  val mainOpt: Opts[IO[Unit]] =
    (nameOpt, alphaOpt, forceOpt).mapN[IO[Unit]] {
      (name, alpha, force) =>
        if (force)
          IO.raiseError(
            new Exception(s"Manually FAIL triggered by $name! alpha=$alpha")
          )
        else printlnio(s"Hello $name. Running with alpha=$alpha")
    }

  val runOpt = versionOpt orElse mainOpt
  val command: Command[IO[Unit]] = Command[IO[Unit]](
    name = "declined",
    header = "Testing decline+zio"
  )(versionOpt orElse mainOpt)


  def run(args: List[String]): IO[ExitCode] =
    argsParser.parse(args) match
      case Left(help) => IO(System.err.println(help)).as(ExitCode.Error)
      case Right((desiredUsername, address)) =>
        Console
          .create[IO]
          .flatMap { implicit console =>
            val st =
              Stream
                .exec(Console[IO].info(s"Connecting $desiredUsername to server $address.")) ++
                Stream
                  .repeatEval(Console[IO].readLine("> "))
                  .flatMap {
                    case Some(txt) => Stream(txt)
                    case None      => Stream.raiseError[IO](new UserQuit)
                  }
                  .map(txt => Protocol.ClientCommand.SendMessage(txt))
                  .through(StreamEncoder.many(Protocol.ClientCommand.codec).toPipeByte)
                  //.chunks
                  .debug()
                  .through(StreamDecoder.many(Protocol.ClientCommand.codec).toPipeByte)
                  .debug()
                  .flatMap((ein) =>
                    Stream
                      .exec(Console[IO].errorln(s"Eingabe war: $ein"))
                  )
            st.compile.drain
          }
          .as(ExitCode.Success)





  def run1(args: List[String]): IO[ExitCode] =
    argsParser.parse(args) match
      case Left(help) => IO(System.err.println(help)).as(ExitCode.Error)
      case Right((desiredUsername, address)) =>
        Console
          .create[IO]
          .flatMap { implicit console =>
            Client
              .start[IO](address, desiredUsername)
              .compile
              .drain
          }
          .as(ExitCode.Success)
