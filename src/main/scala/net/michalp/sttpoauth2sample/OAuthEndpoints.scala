package net.michalp.sttpoauth2sample

import sttp.tapir._
import transport._
import transport.tapirCodecs._

object OAuthEndpoints {

  val loginRedirect: Endpoint[Unit, Unit, RedirectUrl, Any] =
    endpoint
      .get
      .in("api" / "login-redirect")
      .out(header[RedirectUrl]("Location"))
      .out(statusCode(sttp.model.StatusCode.SeeOther))

  val postLogin: Endpoint[(AuthorizationCode, State), Unit, String, Any] =
    endpoint
      .get
      .in("api" / "post-login")
      .in(query[AuthorizationCode]("code"))
      .in(query[State]("state"))
      .out(stringBody)

}
