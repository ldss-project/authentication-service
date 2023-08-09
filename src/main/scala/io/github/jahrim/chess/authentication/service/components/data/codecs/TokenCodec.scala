package io.github.jahrim.chess.authentication.service.components.data.codecs

import io.github.jahrim.chess.authentication.service.components.data.Token
import io.github.jahrim.hexarc.persistence.bson.codecs.{BsonDocumentDecoder, BsonDocumentEncoder}
import io.github.jahrim.hexarc.persistence.bson.dsl.BsonDSL.{*, given}
import org.bson.conversions.Bson

import java.time.Instant

/** [[Bson]] codec for [[User]]. */
object TokenCodec:
  /** A given [[BsonDocumentDecoder]] for [[Token]]. */
  given tokenDecoder: BsonDocumentDecoder[Token] = bson =>
    Token(
      bson.require("id").as[String],
      bson.require("expiration").as[Instant]
    )

  /** A given [[BsonDocumentEncoder]] for [[Token]]. */
  given tokenEncoder: BsonDocumentEncoder[Token] = token =>
    bson {
      "id" :: token.id
      "expiration" :: token.expiration
    }
