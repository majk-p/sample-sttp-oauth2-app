package net.michalp.sttpoauth2sample

import sttp.client3._
import sttp.client3.SttpBackend
import caliban.client.github.Client._
import com.ocadotechnology.sttp.oauth2.Secret
import sttp.model.Header
import cats.implicits._
import cats.effect.Sync

trait Github[F[_]] {
  def repositoryDescription(token: Secret[String])(owner: String, name: String): F[String]
}

object Github {
  def apply[F[_]](implicit ev: Github[F]): Github[F] = ev

  val baseUri = uri"https://api.github.com/graphql"

  def sttpInstance[F[_]: Sync](implicit backend: SttpBackend[F, Any]) = new Github[F] {

    override def repositoryDescription(token: Secret[String])(owner: String, name: String): F[String] = {
      val query = Query.repository(owner, name) {
        Repository.description
      }
      val header = Header("Authorization", s"Bearer ${token.value}")
      query
        .toRequest(baseUri)
        .headers(header)
        .send(backend)
        .map(_.body)
        .rethrow
        .map(_.flatten.getOrElse("Missing description"))
    }

  }

}
