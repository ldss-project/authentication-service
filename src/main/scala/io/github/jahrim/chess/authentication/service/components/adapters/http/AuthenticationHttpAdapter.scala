package io.github.jahrim.chess.authentication.service.components.adapters.http

import io.github.jahrim.chess.authentication.service.components.adapters.http.handlers.LogHandler
import io.github.jahrim.chess.authentication.service.components.data.codecs.Codecs.{*, given}
import io.github.jahrim.chess.authentication.service.components.ports.AuthenticationPort
import io.github.jahrim.chess.authentication.service.components.exceptions.UserNotFoundException
import io.github.jahrim.chess.authentication.service.components.exceptions.IncorrectPasswordException
import io.github.jahrim.chess.authentication.service.components.exceptions.MalformedInputException
import io.github.jahrim.chess.authentication.service.components.exceptions.ExpiredTokenException
import io.github.jahrim.chess.authentication.service.util.extension.JsonObjectExtension.*
import io.github.jahrim.chess.authentication.service.util.extension.RoutingContextExtension.*
import io.github.jahrim.chess.authentication.service.util.extension.VertxFutureExtension.*
import io.github.jahrim.hexarc.persistence.bson.dsl.BsonDSL.{*, given}
import io.github.jahrim.hexarc.architecture.vertx.core.components.{Adapter, AdapterContext}
import io.vertx.core.Handler
import io.vertx.core.http.{HttpServerOptions, HttpServerResponse}
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.{Router, RoutingContext}

import java.lang.Exception

class AuthenticationHttpAdapter(
    options: HttpServerOptions = new HttpServerOptions:
      setHost("localhost")
      setPort(8080)
) extends Adapter[AuthenticationPort]:
  override protected def init(context: AdapterContext[AuthenticationPort]): Unit =
    val router = Router.router(context.vertx)

    router
      .route()
      .handler(BodyHandler.create())
      .handler(LogHandler(context.log.info))

    router
      .get("/")
      .handler { message => message.response().send("Ciao, questo e' l'Authentication Service...") }

    router
      .post("/user/:username/sign-in")
      .handler { message =>
        future {
          val username: String = message.requirePathParam("username")
          val email: String = message.requireBodyParam("user.email").as[String]
          val password: String = message.requireBodyParam("user.password").as[String]
          (username, email, password)
        }
          .compose { context.api.registerUser(_, _, _) }
          .map { token =>
            bsonToJson(bson {
              "user" :: bson {
                "token" :: bson {
                  "id" :: token
                }
              }
            }).encode()
          }
          .onSuccess { json => message.sendJson(json) }
          .onFailure { fail(message) }
      }

    router
      .post("/user/:username/log-in")
      .handler { message =>
        future {
          val username: String = message.requirePathParam("username")
          val password: String = message.requireBodyParam("user.password").as[String]
          (username, password)
        }
          .compose { context.api.loginUser(_, _) }
          .map { token =>
            bsonToJson(bson {
              "user" :: bson {
                "token" :: bson {
                  "id" :: token
                }
              }
            }).encode()
          }
          .onSuccess { json => message.sendJson(json) }
          .onFailure {
            fail(message)
          }

      }

    router
      .get("/user/:username/profile")
      .handler { message =>
        future {
          message.requirePathParam("username")
        }
          .compose {
            context.api.getUserInformation(_)
          }
          .map { user =>
            bsonToJson(bson { "user" :: user }).encode()
          }
          .onSuccess { json => message.sendJson(json) }
          .onFailure {
            fail(message)
          }
      }

    router
      .put("/user/:username/password")
      .handler { message =>
        future {
          val username: String = message.requirePathParam("username")
          val password: String = message.requireBodyParam("user.password").as[String]
          (username, password)
        }
          .compose { context.api.updatePassword(_, _) }
          .onSuccess { ok(message) }
          .onFailure { fail(message) }
      }

    router
      .get("/token/:tokenId/validate")
      .handler { message =>
        future {
          message.requirePathParam("tokenId")
        }
          .compose {
            context.api.validateToken(_)
          }
          .map { user =>
            bsonToJson(bson {
              "user" :: bson {
                "username" :: user
              }
            }).encode()
          }
          .onSuccess { json => message.sendJson(json) }
          .onFailure {
            fail(message)
          }
      }

    router
      .delete("/token/:tokenId/revoke")
      .handler { message =>
        future {
          message.requirePathParam("tokenId")
        }
          .compose {
            context.api.revokeToken(_)
          }
          .onSuccess {
            ok(message)
          }
          .onFailure {
            fail(message)
          }
      }

    context.vertx
      .createHttpServer(options)
      .requestHandler(router)
      .listen(_ => context.log.info("The server is up"))

  end init

  private def ok[T](message: RoutingContext): Handler[T] = _ => message.ok.send()

  private def fail(message: RoutingContext): Handler[Throwable] = e =>
    e.printStackTrace()
    val response: HttpServerResponse = e match
      case e: UserNotFoundException      => message.error(404, e)
      case e: MalformedInputException    => message.error(400, e)
      case e: IncorrectPasswordException => message.error(400, e)
      case e: ExpiredTokenException      => message.error(403, e)
      case e: Exception                  => message.error(500, e)
    response.send()
