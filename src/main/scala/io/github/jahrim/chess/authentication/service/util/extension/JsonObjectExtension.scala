package io.github.jahrim.chess.authentication.service.util.extension

import io.vertx.core.json.JsonObject
import org.bson.BsonDocument
import org.bson.conversions.Bson
import org.bson.json.JsonObject as BsonJsonObject

/** An extension for [[JsonObject]]s. */
object JsonObjectExtension:
  /** A [[Conversion]] from [[Bson]] to [[JsonObject]]. */
  def bsonToJson: Conversion[BsonDocument, JsonObject] = bson => JsonObject(bson.toJson)

  /** A [[Conversion]] from [[JsonObject]] to [[Bson]]. */
  def jsonToBson: Conversion[JsonObject, BsonDocument] = json =>
    BsonJsonObject(json.encode).toBsonDocument()
