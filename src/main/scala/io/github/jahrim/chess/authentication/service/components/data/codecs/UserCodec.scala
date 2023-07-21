package io.github.jahrim.chess.authentication.service.components.data.codecs

import io.github.jahrim.chess.authentication.service.components.data.User
import io.github.jahrim.hexarc.persistence.bson.codecs.{BsonDocumentDecoder, BsonDocumentEncoder}
import io.github.jahrim.hexarc.persistence.bson.dsl.BsonDSL.{*, given}
import org.bson.conversions.Bson

/** [[Bson]] codec for [[User]]. */
object UserCodec:
  /** A given [[BsonDocumentDecoder]] for [[User]]. */
  given bsonToUser: BsonDocumentDecoder[User] = bson =>
    User(
      bson.require("username").as[String],
      bson.require("email").as[String]
    )

  /** A given [[BsonDocumentEncoder]] for [[User]]. */
  given userToBson: BsonDocumentEncoder[User] = user =>
    bson {
      "username" :: user.username
      "email" :: user.email
    }
