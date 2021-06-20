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

  def instance[F[_]: AuthorizationCodeProvider[Uri, *[_]]: Sync]: OAuthRouter[F] = new OAuthRouter[F] {

    private val randomeState = "thisShouldBeRandomTextGeneratedPerRequest"

    def loginRedirect: F[RedirectUrl] = 
      RedirectUrl {  
        AuthorizationCodeProvider[Uri, F]
          .loginLink(state=randomeState.some)
          .toString
      }.pure[F]

    def handleLogin(code: AuthorizationCode, state: State): F[String] = for {
      _ <- Sync[F].delay(println(s"Code: $code, State: $state"))
      // {"access_token":"gho_16C7e42F292c6912E7710c838347Ae178B4a", "scope":"repo,gist", "token_type":"bearer"}
      // authCodeToToken should allow other response models
      // for github we need to add .header(HeaderNames.Accept, "application/json") 
      tokenResponse <- AuthorizationCodeProvider[Uri, F].authCodeToToken[OAuth2TokenResponse](code.value)
      _ <- Sync[F].delay(println(s"token response $tokenResponse"))
    } yield tokenResponse.tokenType
    
  }

}
