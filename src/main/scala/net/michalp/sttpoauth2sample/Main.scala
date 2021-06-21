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

  def authorizationCodeProvider(config: ConfigReader.Config)(implicit backend: SttpBackend[IO, Any]): AuthorizationCodeProvider[Uri, IO] =
    AuthorizationCodeProvider.uriInstance[IO](
      baseUrl = config.oauth2.baseUrl,
      redirectUri = Uri.unsafeParse(s"http://${config.server.host}:${config.server.port}/api/post-login"),
      clientId = config.oauth2.appId,
      clientSecret = Secret(config.oauth2.appSecret),
      pathsConfig = AuthorizationCodeProvider.Config.GitHub
    )

  def runServer(serverConfig: ConfigReader.Config.Server)(routes: HttpRoutes[IO])(ec: ExecutionContext) =
    BlazeServerBuilder[IO](ec)
      .bindHttp(serverConfig.port, serverConfig.host)
      .withHttpApp(Router("/" -> routes).orNotFound)
      .resource

  val program: Resource[IO, Unit] = for {
    implicit0(blocker: Blocker)              <- Blocker[IO]
    config                                   <- Resource.eval(ConfigReader.readConfig[IO])
    implicit0(backend: SttpBackend[IO, Any]) <- AsyncHttpClientCatsBackend.resource[IO]()
    implicit0(provider: AuthorizationCodeProvider[Uri, IO]) = authorizationCodeProvider(config)(backend)
    implicit0(github: Github[IO]) = Github.sttpInstance[IO]
    oauthRoutes = routes(OAuthRouter.instance)
    _                                        <- runServer(config.server)(oauthRoutes)(executionContext)
    _                                        <- Resource.eval(IO(println(s"Server listening on http://${config.server.host}:${config.server.port}")))
  } yield ()

  override def run(args: List[String]): IO[ExitCode] =
    program.use(_ => IO.never).as(ExitCode.Success)
}
