package io.github.jahrim.chess.authentication.service.util.extension

import io.vertx.core.json.JsonObject
import org.bson.conversions.Bson
import org.bson.json.JsonObject as BsonJsonObject

/** An extension for [[JsonObject]]s. */
object JsonObjectExtension:
  /** A [[Conversion]] from [[Bson]] to [[JsonObject]]. */
  def bsonToJson: Conversion[Bson, JsonObject] = bson => JsonObject(bson.toBsonDocument.toJson)

  /** A [[Conversion]] from [[JsonObject]] to [[Bson]]. */
  def jsonToBson: Conversion[JsonObject, Bson] = json => BsonJsonObject(json.encode)
