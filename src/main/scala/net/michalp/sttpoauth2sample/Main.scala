package net.michalp.sttpoauth2sample

import cats.effect._
import cats.syntax.all._
import com.ocadotechnology.sttp.oauth2.AuthorizationCodeProvider
import com.ocadotechnology.sttp.oauth2.AuthorizationCodeProvider.{Config => PathsConfig}
import com.ocadotechnology.sttp.oauth2.Secret
import org.http4s.HttpRoutes
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.syntax.kleisli._
import sttp.capabilities.fs2.Fs2Streams
import sttp.client3._
import sttp.client3.asynchttpclient.cats.AsyncHttpClientCatsBackend
import sttp.model.Uri
import sttp.tapir._
import sttp.tapir.server.http4s.Http4sServerInterpreter

import scala.concurrent.ExecutionContext
import cats.effect.IO

object Main extends IOApp {

  def routes(router: OAuthRouter[IO]): HttpRoutes[IO] =
    Http4sServerInterpreter
      .toRoutes(
        List(
          OAuthEndpoints.loginRedirect.serverLogic(_ => router.loginRedirect.map(_.asRight[Unit])),
          OAuthEndpoints.postLogin.serverLogic(data => (router.handleLogin _).tupled(data).map(_.asRight[Unit]))
        )
      )

  val port = 8080
  val host = "localhost"

  def authorizationCodeProvider(config: ConfigReader.Config)(implicit backend: SttpBackend[IO, Any]): AuthorizationCodeProvider[Uri, IO] =
    AuthorizationCodeProvider.uriInstance[IO](
      baseUrl = Uri.unsafeParse("https://github.com/"),
      redirectUri = Uri.unsafeParse("http://localhost:8080/api/post-login"),
      clientId = config.appId,
      clientSecret = Secret(config.appSecret),
      pathsConfig = AuthorizationCodeProvider.Config.GitHub
    )

  def runServer(host: String, port: Int)(routes: HttpRoutes[IO])(ec: ExecutionContext) =
    BlazeServerBuilder[IO](ec)
      .bindHttp(port, host)
      .withHttpApp(Router("/" -> routes).orNotFound)
      .resource

  val program: Resource[IO, Unit] = for {
    implicit0(blocker: Blocker)              <- Blocker[IO]
    config                                   <- Resource.eval(ConfigReader.readConfig[IO])
    implicit0(backend: SttpBackend[IO, Any]) <- AsyncHttpClientCatsBackend.resource[IO]()
    implicit0(provider: AuthorizationCodeProvider[Uri, IO]) = authorizationCodeProvider(config)(backend)
    oauthRoutes = routes(OAuthRouter.instance)
    _                                        <- runServer(host, port)(oauthRoutes)(executionContext)
    _                                        <- Resource.eval(IO(println(s"Server listening on http://$host:$port")))
  } yield ()

  override def run(args: List[String]): IO[ExitCode] =
    program.use(_ => IO.never).as(ExitCode.Success)
}
