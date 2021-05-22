package net.michalp.sttpoauth2sample

import cats.effect._
import sttp.client3._
import org.http4s.HttpRoutes
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.syntax.kleisli._
import sttp.tapir._
import sttp.tapir.server.http4s.Http4sServerInterpreter
import cats.syntax.all._

import scala.concurrent.ExecutionContext

object Endpoints {

  val helloWorld: Endpoint[String, Unit, String, Any] =
    endpoint.get.in("hello").in(query[String]("name")).out(stringBody)

}

object Handlers {

  def helloWorld(name: String) =
    IO(s"Hello, $name!".asRight[Unit]) 
  
  
}

object Main extends App {

  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
  
  // TODO: should be possible to get rid of those in CE3
  implicit val contextShift: ContextShift[IO] = IO.contextShift(ec)
  implicit val timer: Timer[IO] = IO.timer(ec)

  
  val helloWorldRoutes: HttpRoutes[IO] = 
    Http4sServerInterpreter
      .toRoutes(Endpoints.helloWorld)(Handlers.helloWorld)

  // starting the server

  val port = 8080
  val host = "localhost"

  BlazeServerBuilder[IO](ec)
    .bindHttp(port, host)
    .withHttpApp(Router("/" -> helloWorldRoutes).orNotFound)
    .resource
    .use { _ =>
      IO {
        println(s"Server listening on http://$host:$port")
      }.flatMap(_ => IO.never)
    }
    .unsafeRunSync()
}