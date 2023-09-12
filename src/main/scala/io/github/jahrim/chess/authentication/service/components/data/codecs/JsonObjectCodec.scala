/*
MIT License

Copyright (c) 2023 Cesario Jahrim Gabriele, Kentpayeva Madina

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
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
