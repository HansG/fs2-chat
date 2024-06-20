package fs2chat.aiTry

import cats.effect.{IO, IOApp}
import sttp.tapir.*
import io.circe.generic.auto.*
import org.http4s.ember.server.EmberServerBuilder
import com.comcast.ip4s.{Host, Port}
import org.http4s.{HttpApp, HttpRoutes}
import sttp.tapir.server.http4s.Http4sServerInterpreter

import javax.xml.transform.Templates

//import sttp.tapir.files.*
//import sttp.tapir.server.jdkhttp.*
import java.util.concurrent.Executors
import cats.effect.{IO, IOApp}
import sttp.tapir.json.circe._
import cats.effect._
import io.circe.generic.auto._
import scala.concurrent.ExecutionContext
//import org.http4s.ember.server.EmberServerBuilder
//import org.http4s.implicits._
//import sttp.tapir.server.http4s.Http4sServerInterpreter
//import org.http4s._

object Http:
  private val index =
    endpoint.get
      .out(htmlBodyUtf8)
    .serverLogic(_ =>
      IO(Right("<html><body><h1>Index Page</h1></body></html>")) )
//      .serverLogic(_ => Right(Templates.index()))

  private def inquire(using Config, Db) =
    endpoint.post
      .in("inquire")
      .in(formBody[Map[String, String]])
      .out(htmlBodyUtf8)
      .serverLogic { form =>
        form.get("q").flatMap(s => if s.isBlank() then None else Some(s)) match
          case Some(question) =>
            val response = AI.askDocs(question)
            val rendered = MD.render(response)

            IO(Right(rendered))
//            Right(UserResponse(rendered))

          case None => 
            IO(Right("Have nothing to ask?"))
//            Right(Templates.response("Have nothing to ask?"))
      }


  def startServer()(using cfg: Config, db: Db) =
    JdkHttpServer()
      .executor(Executors.newVirtualThreadPerTaskExecutor())
      .addEndpoint(staticResourcesGetServerEndpoint("static")(classOf[App].getClassLoader, "/"))
      .addEndpoint(inquire)
      .addEndpoint(index)
      .port(cfg.port)
      .start()

  given c:Config = Config.apply

  val ds: javax.sql.DataSource = ???
  given d : Db = Db(ds)
  
  val helloRoute: HttpRoutes[IO] = Http4sServerInterpreter[IO]().toRoutes(List(index, inquire))
  // Compile the routes to an HttpApp
  val httpApp: HttpApp[IO] = helloRoute.orNotFound
  
  // Assemble and run the server
  override def run: IO[Unit] =
    EmberServerBuilder.default[IO]
      .withHost(Host.fromString("localhost").get)
      .withPort(Port.fromString("8080").get)
      .withHttpApp(httpApp)
      .build
      .use(_ => IO.never) // Keep the server running indefinitely
