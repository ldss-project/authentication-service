package io.github.jahrim.chess.authentication.service.components.data.codecs

import io.github.jahrim.chess.authentication.service.components.data.User
import io.github.jahrim.hexarc.persistence.bson.codecs.{BsonDocumentDecoder, BsonDocumentEncoder}
import io.github.jahrim.hexarc.persistence.bson.dsl.BsonDSL.{*, given}

object UserCodec:
  given bsonToUser: BsonDocumentDecoder[User] = bson =>
    User(
      bson.require("username").as[String],
      bson.require("email").as[String]
    )

  given userToBson: BsonDocumentEncoder[User] = user =>
    bson {
      "username" :: user.username
      "email" :: user.email
    }
