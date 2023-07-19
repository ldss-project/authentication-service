package io.github.jahrim.chess.authentication.service.util.extension

import io.github.jahrim.chess.authentication.service.util.extension.JsonObjectExtension.jsonToBson
import io.github.jahrim.hexarc.persistence.bson.dsl.BsonDSL.{*, given}
import io.vertx.core.http.HttpServerResponse
import io.vertx.ext.web.RoutingContext
import org.bson.BsonValue

import scala.util.Try

/** An extension for [[RoutingContext]]s. */
object RoutingContextExtension:
  extension (self: RoutingContext) {

    /** @return a '200 OK' http response. */
    def ok: HttpServerResponse =
      self.response.setStatusCode(200)

    /**
     * @param statusCode the specified http status code.
     * @param exception  the specified [[Exception]].
     * @return an http error response the specified http status code and the
     *         specified [[Exception]] as the cause of the error.
     */
    def error(statusCode: Int, exception: Exception): HttpServerResponse =
      self.response.setStatusCode(statusCode).setStatusMessage(exception.getMessage)

    /**
     * @param json the specified json string.
     * @return a '200 OK' http response with the specified json as content.
     */
    def sendJson(json: String): Unit =
      self.ok.putHeader("Content-Type", "application/json").send(json)

    /**
     * Get the value of specified path parameter if present.
     *
     * @param paramName the name of the specified path parameter.
     * @return the value of the specified path parameter.
     * @throws IllegalArgumentException if no value is bound to the specified path parameter.
     */
    def requirePathParam(paramName: String): String =
      Option(self.pathParam(paramName)).getOrElse {
        throw IllegalArgumentException(s"Request missing path parameter '$paramName'.")
      }

    /**
     * Get the value of specified body parameter if present.
     *
     * @param path the path to the specified body parameter.
     * @return the value of the specified body parameter.
     * @throws IllegalArgumentException if no value is bound to the specified body parameter.
     */
    def requireBodyParam(path: String): BsonValue =
      Try {
        jsonToBson(self.body.asJsonObject).require(path)
      }.getOrElse {
        throw IllegalArgumentException(s"Request missing body parameter '$path'.")
      }
  }
