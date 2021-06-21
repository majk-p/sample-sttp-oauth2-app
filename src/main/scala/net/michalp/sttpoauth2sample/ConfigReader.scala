package net.michalp.sttpoauth2sample

import cats.implicits._
import cats.effect.Sync
import sttp.model.Uri

object ConfigReader {

  def readConfig[F[_]: Sync]: F[Config] = Sync[F]
    .delay {
      (
        Uri.parse("https://github.com/").toOption,
        sys.env.get(appIdEnvVariable),
        sys.env.get(appSecretEnvVariable)
      ).mapN(Config.OAuth2.apply)
    }
    .flatMap { maybeOAuth2Config =>
      Sync[F].catchNonFatal(Config(maybeOAuth2Config.get, Config.Server("localhost", 8080)))
    }

  val appIdEnvVariable = "APP_ID"
  val appSecretEnvVariable = "APP_SECRET"

  final case class Config(
    oauth2: Config.OAuth2,
    server: Config.Server
  )

  object Config {

    final case class Server(
      host: String,
      port: Int
    )

    final case class OAuth2(
      baseUrl: Uri,
      appId: String,
      appSecret: String
    )

  }

}
