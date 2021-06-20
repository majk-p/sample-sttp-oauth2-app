package net.michalp.sttpoauth2sample

import transport._
import org.http4s.HttpRoutes

import cats.implicits._
import com.ocadotechnology.sttp.oauth2.AuthorizationCodeProvider
import sttp.model.Uri
import sttp.tapir.server.http4s.Http4sServerInterpreter
import cats.effect.Sync
import com.ocadotechnology.sttp.oauth2.OAuth2TokenResponse

trait OAuthRouter[F[_]] {
  def loginRedirect: F[RedirectUrl]
  def handleLogin(code: AuthorizationCode, state: State): F[String]
}

object OAuthRouter {

  def apply[F[_]](implicit ev: OAuthRouter[F]): OAuthRouter[F] = ev

  def instance[F[_]: Github: AuthorizationCodeProvider[Uri, *[_]]: Sync]: OAuthRouter[F] = new OAuthRouter[F] {

    private val randomeState = "thisShouldBeRandomTextGeneratedPerRequest"

    def loginRedirect: F[RedirectUrl] =
      RedirectUrl {
        AuthorizationCodeProvider[Uri, F]
          .loginLink(state = randomeState.some)
          .toString
      }.pure[F]

    def handleLogin(code: AuthorizationCode, state: State): F[String] = for {
      _               <- Sync[F].delay(println(s"Returning user for state: $state"))
      tokenResponse   <- AuthorizationCodeProvider[Uri, F].authCodeToToken[OAuth2TokenResponse](code.value)
      _               <- Sync[F].delay(println(s"Requesting description for https://github.com/majk-p/sample-sttp-oauth2-app/"))
      repoDescription <- Github[F].repositoryDescription(tokenResponse.accessToken)("majk-p", "sample-sttp-oauth2-app")
    } yield repoDescription

  }

}
