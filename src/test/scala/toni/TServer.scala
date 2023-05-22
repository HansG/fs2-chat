//> using scala "3.2.1"
//> using resourceDir "."
//> using packaging.packageType "assembly"
//> using lib "org.http4s::http4s-ember-server::0.23.18"
//> using lib "org.http4s::http4s-dsl::0.23.18"
//> using lib "com.monovore::decline-effect::2.4.1"
//> using lib "ch.qos.logback:logback-classic:1.4.5"
//> using mainClass "toni.TServer"


package toni

import cats.effect.{ExitCode, IO}
import cats.syntax.all.*
import com.comcast.ip4s.{ipv4, port}
import com.monovore.decline.Opts
import com.monovore.decline.effect.CommandIOApp
import org.http4s.Uri
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.middleware.CORS
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jFactory
import org.typelevel.log4cats.slf4j.loggerFactoryforSync

//https://toniogela.dev/http4s-on-fly-io/
object TServer extends CommandIOApp("helloServer", "Greets you in HTML") {

  val titleOpt: Opts[String] =
    Opts.env[String]("TITLE", "Page title").withDefault("Hello")

  val baseUrlOpt: Opts[Uri] = Opts
    .env[String]("BASE_URL", "The base url").withDefault("http://localhost:8080/hello/HG")
    .mapValidated(
      Uri
        .fromString(_)
        .leftMap(_.message)
        .ensure("base url must be absolute")(_.path.addEndsWithSlash.absolute)
        .map(uri => uri.withPath(uri.path.dropEndsWithSlash))
        .toValidatedNel
    )

  //BASE_URL="https://toniogela.dev" scala-cli run TServer.scala   //-> localhost:8080/foo   ....zum Webserver API testen mit json Daten siehe https://reqbin.com/
  //BASE_URL="https://toniogela.dev" scala-cli run .    ->genügt, wenn nur eine Möglichkeit auf Pfad..??
  def main: Opts[IO[ExitCode]] = (baseUrlOpt, titleOpt).mapN((baseUrl, title) =>
    for {
      given Logger[IO] <- Slf4jFactory.create[IO]
      exitCode <- EmberServerBuilder
        .default[IO]
        .withHttp2
        .withHost(ipv4"0.0.0.0")
        .withPort(port"8080")
        .withHttpApp(
          CORS.policy.withAllowOriginAll(TRoutes.routes[IO](baseUrl, title)).orNotFound
        )
        .build
        .useForever
        .as(ExitCode.Success)
    } yield exitCode
  )
  
  
  
}