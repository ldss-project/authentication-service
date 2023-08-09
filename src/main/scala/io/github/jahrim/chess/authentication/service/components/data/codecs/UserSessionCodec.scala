package io.github.jahrim.chess.authentication.service.components.data.codecs

import io.github.jahrim.chess.authentication.service.components.data.codecs.TokenCodec.given
import io.github.jahrim.chess.authentication.service.components.data.{Token, UserSession}
import io.github.jahrim.hexarc.persistence.bson.codecs.{BsonDocumentDecoder, BsonDocumentEncoder}
import io.github.jahrim.hexarc.persistence.bson.dsl.BsonDSL.{*, given}
import org.bson.conversions.Bson

/** [[Bson]] codec for [[UserSession]]. */
object UserSessionCodec:
  /** A given [[BsonDocumentDecoder]] for [[UserSession]]. */
  given userSessionDecoder: BsonDocumentDecoder[UserSession] = bson =>
    UserSession(
      bson.require("username").as[String],
      bson.require("token").as[Token]
    )

  /** A given [[BsonDocumentEncoder]] for [[UserSession]]. */
  given userSessionEncoder: BsonDocumentEncoder[UserSession] = session =>
    bson {
      "username" :: session.username
      "token" :: session.token
    }
