package net.michalp.sttpoauth2sample

import cats.implicits._
import cats.effect.Sync

object ConfigReader {

  def readConfig[F[_]: Sync]: F[Config] = Sync[F]
    .delay {
      (
        sys.env.get(appIdEnvVariable),
        sys.env.get(appSecretEnvVariable)
      ).mapN(Config.apply)
    }
    .flatMap(maybeConfig => Sync[F].catchNonFatal(maybeConfig.get))

  val appIdEnvVariable = "APP_ID"
  val appSecretEnvVariable = "APP_SECRET"

  final case class Config(
    appId: String,
    appSecret: String
  )

}
