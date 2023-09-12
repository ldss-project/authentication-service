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
package io.github.jahrim.chess.authentication.service.components.adapters.http

import io.github.jahrim.chess.authentication.service.components.adapters.http.AuthenticationHttpAdapter.*
import io.github.jahrim.chess.authentication.service.components.adapters.http.AuthenticationHttpAdapter.given
import io.github.jahrim.chess.authentication.service.components.adapters.http.handlers.LogHandler
import io.github.jahrim.chess.authentication.service.components.data.UserSession
import io.github.jahrim.chess.authentication.service.components.data.codecs.Codecs.given
import io.github.jahrim.chess.authentication.service.components.data.codecs.JsonObjectCodec.*
import io.github.jahrim.chess.authentication.service.components.exceptions.*
import io.github.jahrim.chess.authentication.service.components.ports.AuthenticationPort
import io.github.jahrim.hexarc.architecture.vertx.core.components.{Adapter, AdapterContext}
import io.github.jahrim.hexarc.persistence.bson.dsl.BsonDSL.{*, given}
import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.http.{Cookie, CookieSameSite, HttpMethod, HttpServerOptions}
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.handler.{BodyHandler, CorsHandler}
import io.vertx.ext.web.{Router, RoutingContext}
import org.bson.{BsonDocument, BsonValue}

import java.time.Instant
import java.net.{URLDecoder, URLEncoder}
import java.nio.charset.StandardCharsets
import java.time.temporal.ChronoUnit
import scala.jdk.CollectionConverters.{SeqHasAsJava, SetHasAsJava}
import scala.util.Try

/**
 * An [[Adapter]] for the authentication service, that allows interactions through
 * the http protocol with the [[AuthenticationPort]].
 * @param httpOptions the specified http options.
 * @param allowedOrigins a sequence of sites that are allowed to use the api of this service.
 */
class AuthenticationHttpAdapter(
    httpOptions: HttpServerOptions = HttpServerOptions().setHost("localhost").setPort(8080),
    allowedOrigins: Seq[String] = Seq()
) extends Adapter[AuthenticationPort]:
  override protected def init(context: AdapterContext[AuthenticationPort]): Unit =
    val router = Router.router(context.vertx)

    if allowedOrigins.nonEmpty then
      router
        .route()
        .handler(
          CorsHandler
            .create()
            .addOrigins(allowedOrigins.asJava)
            .allowCredentials(true)
            .allowedMethods(
              Set(
                HttpMethod.HEAD,
                HttpMethod.GET,
                HttpMethod.POST,
                HttpMethod.PUT
              ).asJava
            )
        )

    router
      .route()
      .handler(BodyHandler.create())
      .handler(LogHandler(context.log.info))
      .failureHandler(context => context.sendException(context.failure))

    router
      .get("/")
      .handler { message => message.response().send("Welcome to the Authentication Service!") }

    router
      .post("/user/:username/sign-in")
      .handler { message =>
        val username: String = message.requirePathParam("username")
        val email: String = message.requireBodyParam("user.email").as[String]
        val password: String = message.requireBodyParam("user.password").as[String]
        context.api
          .registerUser(username, email, password)
          .onSuccess(session =>
            message.saveSession(session)
            message.sendBson(HttpResponseStatus.OK, bson { "session" :: session })
          )
          .onFailure(message.sendException)
      }

    router
      .post("/user/:username/log-in")
      .handler { message =>
        val username: String = message.requirePathParam("username")
        val password: String = message.requireBodyParam("user.password").as[String]
        context.api
          .loginUser(username, password)
          .onSuccess(session =>
            message.saveSession(session)
            message.sendBson(HttpResponseStatus.OK, bson { "session" :: session })
          )
          .onFailure(message.sendException)
      }

    router
      .post("/user/:username/log-out")
      .handler { message =>
        val session: UserSession = message.requireSession()
        context.api
          .revokeToken(session.token)
          .onSuccess(_ =>
            message.deleteSession()
            message.sendOk()
          )
          .onFailure(message.sendException)
      }

    router
      .get("/user/:username/profile")
      .handler { message =>
        val session: UserSession = message.requireSession()
        val username: String = message.requirePathParam("username")
        context.api
          .validateToken(session.token, username)
          .compose(_ => context.api.getUserInformation(username))
          .onSuccess(user => message.sendBson(HttpResponseStatus.OK, bson { "user" :: user }))
          .onFailure(message.sendException)
      }

    router
      .put("/user/:username/password")
      .handler { message =>
        val session: UserSession = message.requireSession()
        val username: String = message.requirePathParam("username")
        val password: String = message.requireBodyParam("user.password").as[String]
        context.api
          .validateToken(session.token, username)
          .compose(_ => context.api.updatePassword(username, password))
          .onSuccess(_ => message.sendOk())
          .onFailure(message.sendException)
      }

    context.vertx
      .createHttpServer(httpOptions)
      .requestHandler(router)
      .listen(_ =>
        context.log.info(s"The server is up at ${httpOptions.getHost}:${httpOptions.getPort}")
      )
  end init

/** Companion object of [[AuthenticationHttpAdapter]]. */
object AuthenticationHttpAdapter:
  private val SessionCookieName: String = "chess-app-session"

  given Conversion[HttpResponseStatus, Int] = _.code()

  extension (self: RoutingContext) {

    /** Send a '200 OK' http response. */
    private def sendOk(): Unit =
      self.response.setStatusCode(HttpResponseStatus.OK).send()

    /**
     * Send an http response with the specified status code and the specified
     * [[BsonDocument]] as json content.
     *
     * @param statusCode the specified status code.
     * @param bson       the specified [[BsonDocument]].
     */
    private def sendBson(statusCode: Int, bson: BsonDocument): Unit =
      self.response
        .setStatusCode(statusCode)
        .putHeader("Content-Type", "application/json")
        .send(bsonToJson(bson).encode())

    /**
     * Send the specified [[Throwable]] as an http response
     * [[BsonDocument]] as json content.
     *
     * @param throwable the specified [[Throwable]].
     */
    private def sendException(throwable: Throwable): Unit =
      throwable.printStackTrace()
      self.sendBson(
        statusCode = throwable match {
          case _: MalformedInputException       => HttpResponseStatus.BAD_REQUEST
          case _: IncorrectPasswordException    => HttpResponseStatus.BAD_REQUEST
          case _: UsernameAlreadyTakenException => HttpResponseStatus.BAD_REQUEST
          case _: TokenExpiredException         => HttpResponseStatus.FORBIDDEN
          case _: UserNotAuthorizedException    => HttpResponseStatus.FORBIDDEN
          case _: UserNotFoundException         => HttpResponseStatus.NOT_FOUND
          case _: Throwable                     => HttpResponseStatus.INTERNAL_SERVER_ERROR
        },
        bson = bson {
          "type" :: throwable.getClass.getSimpleName
          "message" :: throwable.getMessage
        }
      )

    /**
     * Save the specified [[UserSession]] in the session of this context.
     * @param session the specified [[UserSession]].
     */
    private def saveSession(session: UserSession): Unit =
      self.response.addCookie(
        Cookie
          .cookie(
            SessionCookieName,
            URLEncoder.encode(
              bsonToJson(session.asBson.asDocument).encode(),
              StandardCharsets.UTF_8.name()
            )
          )
          .setSecure(true)
          .setSameSite(CookieSameSite.NONE)
          .setMaxAge(ChronoUnit.SECONDS.between(Instant.now(), session.token.expiration))
      )

    /** Delete the session of this context. */
    private def deleteSession(): Unit =
      self.response.removeCookies(SessionCookieName)

    /**
     * Get the value of specified path parameter if present.
     *
     * @param paramName the name of the specified path parameter.
     * @return the value of the specified path parameter.
     * @throws MalformedInputException if no value is bound to the specified path parameter.
     */
    private def requirePathParam(paramName: String): String =
      Option(self.pathParam(paramName)).getOrElse {
        throw MalformedInputException(s"Request missing path parameter '$paramName'.")
      }

    /**
     * Get the value of specified body parameter if present.
     *
     * @param path the path to the specified body parameter.
     * @return the value of the specified body parameter.
     * @throws MalformedInputException if no value is bound to the specified body parameter.
     */
    private def requireBodyParam(path: String): BsonValue =
      Try {
        jsonToBson(self.body.asJsonObject).require(path)
      }.getOrElse {
        throw MalformedInputException(s"Request missing body parameter '$path'.")
      }

    /**
     * Get the value of the session if present.
     *
     * @return the value of the session.
     * @throws UserNotAuthorizedException if the session is not present.
     */
    private def requireSession(): UserSession =
      Option(self.request.getCookie(SessionCookieName))
        .map(cookie => URLDecoder.decode(cookie.getValue, StandardCharsets.UTF_8.name()))
        .map(cookieValue => jsonToBson(JsonObject(cookieValue)).as[UserSession])
        .getOrElse(throw UserNotAuthorizedException())
  }
