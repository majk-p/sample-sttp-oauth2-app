package net.michalp.sttpoauth2sample

import sttp.client3._
import sttp.client3.SttpBackend
import sttp.client3.circe._
import com.ocadotechnology.sttp.oauth2.Secret
import sttp.model.Header
import cats.implicits._
import cats.effect.Sync
import io.circe.Codec
import io.circe.generic.semiauto._

trait Github[F[_]] {
  def userInfo(token: Secret[String]): F[Github.UserInfo]
}

object Github {
  def apply[F[_]](implicit ev: Github[F]): Github[F] = ev

  val baseUri = uri"https://api.github.com/"

  final case class UserInfo(
    login: String,
    url: String,
    email: Option[String],
    name: String
  )

  object UserInfo {
    implicit val codec: Codec[UserInfo] = deriveCodec
  }

  def sttpInstance[F[_]: Sync](implicit backend: SttpBackend[F, Any]) = new Github[F] {

    override def userInfo(token: Secret[String]): F[UserInfo] = {
      val header = Header("Authorization", s"Bearer ${token.value}")
      basicRequest
        .get(baseUri.withPath("user"))
        .headers(header)
        .response(asJson[UserInfo])
        .send()
        .map(_.body)
        .rethrow
    }

  }

}
