package net.michalp.sttpoauth2sample

import sttp.tapir.Codec
import sttp.tapir.CodecFormat.TextPlain

object transport {

  final case class AuthorizationCode(value: String) extends AnyVal
  final case class State(value: String) extends AnyVal
  final case class RedirectUrl(value: String) extends AnyVal

  object tapirCodecs {
    implicit val autorizationCodeCodec: Codec[String, AuthorizationCode, TextPlain] =
      Codec.string.map(AuthorizationCode(_))(_.value)
    implicit val stateCodec: Codec[String, State, TextPlain] =
      Codec.string.map(State(_))(_.value)
    implicit val redirectUrlCodec: Codec[String, RedirectUrl, TextPlain] =
      Codec.string.map(RedirectUrl(_))(_.value)
  }

}
