package toni

import cats.effect.kernel.Async
import org.http4s.{HttpRoutes, MediaType, Response, Status, Uri}
import org.http4s.dsl.io.*
import org.http4s.headers.`Content-Type`
import cats.syntax.all.*
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.*

object TRoutes {

  def page(uri: Uri, title: String): String =
    s"""|<html>
        |<head><title>$title</title></head>
        |<body>Hello from ${uri.toString}</body>
        |</html>""".stripMargin

  def routes[F[_]: Async :Logger](baseUrl: Uri, title: String): HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root / "health" => Response[F](Status.Ok).pure[F]
      case GET -> path =>
        Logger[F].info(s"Serving $path") >>
        Response[F](Status.Ok)
          .withEntity(page(baseUrl.withPath(baseUrl.path.merge(path)), title))
          .withContentType(`Content-Type`(MediaType.text.html))
          .pure[F]
    }
}
