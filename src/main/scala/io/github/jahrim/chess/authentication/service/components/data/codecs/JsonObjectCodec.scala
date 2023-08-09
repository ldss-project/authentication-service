package io.github.jahrim.chess.authentication.service.components.data.codecs

import io.github.jahrim.hexarc.persistence.bson.codecs.{BsonDocumentDecoder, BsonDocumentEncoder}
import io.github.jahrim.hexarc.persistence.bson.dsl.BsonDSL.{*, given}
import io.vertx.core.json.JsonObject
import org.bson.BsonDocument
import org.bson.conversions.Bson
import org.bson.json.JsonObject as BsonJsonObject

/** [[Bson]] codec for [[JsonObject]]. */
object JsonObjectCodec:
  /** A given [[BsonDocumentDecoder]] for [[JsonObject]]. */
  given jsonBsonDecoder: BsonDocumentDecoder[JsonObject] = bson => JsonObject(bson.toJson)

  /** A given [[BsonDocumentEncoder]] for [[JsonObject]]. */
  given jsonBsonEncoder: BsonDocumentEncoder[JsonObject] = json =>
    BsonJsonObject(json.encode).toBsonDocument

  /**
   * @param bson the specified [[BsonDocument]].
   * @return a new [[JsonObject]] corresponding to the specified [[BsonDocument]].
   */
  def bsonToJson(bson: BsonDocument): JsonObject = jsonBsonDecoder(bson)

  /**
   * @param json the specified [[JsonObject]].
   * @return a new [[BsonDocument]] corresponding to the specified [[JsonObject]].
   */
  def jsonToBson(json: JsonObject): BsonDocument = jsonBsonEncoder(json).asDocument
