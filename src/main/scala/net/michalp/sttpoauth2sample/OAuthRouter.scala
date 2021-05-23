package net.michalp.sttpoauth2sample

import transport._
import org.http4s.HttpRoutes
import cats.Monad
import cats.implicits._
import com.ocadotechnology.sttp.oauth2.AuthorizationCodeProvider
import sttp.model.Uri
import sttp.tapir.server.http4s.Http4sServerInterpreter

trait OAuthRouter[F[_]] {
  def loginRedirect: F[RedirectUrl]
  def handleLogin(code: AuthorizationCode, state: State): F[String]
}

object OAuthRouter {

  def apply[F[_]](implicit ev: OAuthRouter[F]): OAuthRouter[F] = ev

  def instance[F[_]: AuthorizationCodeProvider[Uri, *[_]]: Monad]: OAuthRouter[F] = new OAuthRouter[F] {

    private val randomeState = "thisShouldBeRandomTextGeneratedPerRequest"

    def loginRedirect: F[RedirectUrl] = 
      RedirectUrl {  
        AuthorizationCodeProvider[Uri, F]
          .loginLink(state=randomeState.some)
          .toString
      }.pure[F]

    def handleLogin(code: AuthorizationCode, state: State): F[String] = for {
      tokenResponse <- AuthorizationCodeProvider[Uri, F].authCodeToToken(code.value)
    } yield tokenResponse.userId
    
  }

}
