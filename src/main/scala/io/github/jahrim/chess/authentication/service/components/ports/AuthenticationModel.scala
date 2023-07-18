package io.github.jahrim.chess.authentication.service.components.ports

import at.favre.lib.crypto.bcrypt.BCrypt
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates.*
import com.mongodb.client.{MongoClients, MongoCollection}
import com.mongodb.{ConnectionString, MongoClientSettings, ServerApi, ServerApiVersion}
import io.github.jahrim.chess.authentication.service.components.exceptions.UserNotFoundException
import io.github.jahrim.chess.authentication.service.components.ports.AuthenticationPort.User
import io.github.jahrim.chess.authentication.service.components.*
import io.github.jahrim.chess.authentication.service.components.adapters.http.AuthenticationHttpAdapter
import io.github.jahrim.hexarc.architecture.vertx.core.components.PortContext
import io.github.jahrim.hexarc.architecture.vertx.core.dsl.VertxDSL.*
import io.github.jahrim.hexarc.persistence.bson.PersistentDocumentCollection
import io.github.jahrim.hexarc.persistence.bson.dsl.BsonDSL.{*, given}
import io.vertx.core.{Future, Promise, Vertx}
import org.bson.{BsonDocument, BsonTimestamp, BsonValue, Document}
import io.github.jahrim.chess.authentication.service.components.exceptions.*
import java.nio.charset.StandardCharsets
import java.sql.Timestamp
import java.time.temporal.ChronoUnit
import java.time.{Instant, ZonedDateTime}
import java.util.{Date, UUID}
import scala.util.{Failure, Random, Using}

class AuthenticationModel(users: PersistentDocumentCollection) extends AuthenticationPort:
  override protected def init(context: PortContext): Unit = {}

  override def registerUser(
      username: String,
      email: String,
      passwordInput: String
  ): Future[String] =
    context.vertx.executeBlocking { promise =>
      val hashString = BCrypt.withDefaults.hashToString(10, passwordInput.toCharArray)
      println(hashString)
      val randomToken = UUID.randomUUID().toString
      println(s"randomToken: $randomToken")
      users
        .create(bson {
          "username" :: username
          "password" :: hashString
          "email" :: email
          "token" :: bson {
            "id" :: randomToken
            "expiration" :: ZonedDateTime.now.plus(30, ChronoUnit.MINUTES)
          }
        })
        .fold(
          exception => promise.fail(UserNotFoundException()),
          success => promise.complete(randomToken)
        )
    }
  override def loginUser(username: String, password: String): Future[String] =
    context.vertx.executeBlocking { promise =>
      users
        .read( Filters.eq("username", username))
        .fold(
          exception => promise.fail(UserNotFoundException()),
          userInfos =>
            println(userInfos)
            val passwordVerify = BCrypt.verifyer.verify(
              password.toCharArray,
              userInfos.head.require("password").as[String]
            )
            println(passwordVerify)
            if passwordVerify.verified then
              val tokenId: String = UUID.randomUUID().toString
              users
                .update(
                  Filters.eq("username", username),
                  combine(
                    set("token.id", tokenId),
                    set(
                      "token.expiration",
                      ZonedDateTime.now.plus(30, ChronoUnit.MINUTES).toInstant
                    )
                  )
                )
                .fold(
                  exception => promise.fail(exception.getMessage),
                  success => promise.complete(tokenId)
                )
            else promise.fail(IncorrectPasswordException())
        )
    }

  override def getUserInformation(username: String): Future[AuthenticationPort.User] =
    context.vertx.executeBlocking { promise =>
      users
        .read(bson { "username" :: username })
        .fold(
          exception => promise.fail(UserNotFoundException()),
          userInfos =>
            promise.complete(
              User(
                userInfos.head.require("username"),
                userInfos.head.require("email")
              )
            )
        )
    }

  override def updatePassword(username: String, password: String): Future[Unit] =
    context.vertx.executeBlocking { promise =>
      users
        .update(
          Filters.eq("username", username),
          combine(
            set("password", BCrypt.withDefaults.hashToString(10, password.toCharArray))
          )
        )
        .fold(
          exception => promise.fail(UserNotFoundException()),
          success => promise.complete()
        )
    }

  override def validateToken(tokenId: String): Future[String] =
    context.vertx.executeBlocking { promise =>
      users
        .read(Filters.eq("token.id", tokenId))
        .fold(
          exception => promise.fail(exception.getMessage),
          userInfos =>
            println(userInfos.head)
            println(userInfos.head.require("username").as[String])
            println(
              userInfos.head.require("_id").asObjectId.getValue
            )
            println(ZonedDateTime.now.plus(30, ChronoUnit.MINUTES))

            val date: ZonedDateTime = userInfos.head.require("token.expiration")
            if ZonedDateTime.now().isBefore(date) then {
              println("token is not expired")
              println(userInfos.head.require("username").as[String])
              promise.complete(userInfos.head.require("username").as[String])
            } else
              println("token expired!")
              promise.fail(ExpiredTokenException())
        )

    }

  override def revokeToken(tokenId: String): Future[String] =
    context.vertx.executeBlocking { promise =>
      users
        .read(
          Filters.eq("token.id", tokenId)
        )
        .fold(
          exception => promise.fail(UserNotFoundException()),
          userInfo =>
            val date: ZonedDateTime = userInfo.head.require("token.expiration")
            if ZonedDateTime.now().isBefore(date) then {
              println("token is not expired")
              println(userInfo.head.require("username").as[String])
            } else {
              println("token expired!")
              users.update(
                Filters.eq("token.id", tokenId),
                combine(
                  unset("token.id")
                )
              )
              promise.complete(userInfo.head.require("username").as[String])
            }
        )
    }
object AuthenticationModel:

  private case class User(email: String, password: String)
