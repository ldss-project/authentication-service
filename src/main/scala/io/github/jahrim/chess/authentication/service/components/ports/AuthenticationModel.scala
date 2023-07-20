package io.github.jahrim.chess.authentication.service.components.ports

import at.favre.lib.crypto.bcrypt.BCrypt
import com.mongodb.client.model.{Filters, Projections}
import com.mongodb.client.model.Updates.*
import com.mongodb.client.{MongoClients, MongoCollection}
import com.mongodb.{ConnectionString, MongoClientSettings, ServerApi, ServerApiVersion}
import io.github.jahrim.chess.authentication.service.components.exceptions.UserNotFoundException
import io.github.jahrim.chess.authentication.service.components.exceptions.MalformedInputException
import io.github.jahrim.chess.authentication.service.components.exceptions.ExpiredTokenException
import io.github.jahrim.chess.authentication.service.components.exceptions.IncorrectPasswordException
import io.github.jahrim.chess.authentication.service.components.data.*
import io.github.jahrim.chess.authentication.service.components.*
import io.github.jahrim.chess.authentication.service.components.data.codecs.Codecs.{*, given}
import io.github.jahrim.hexarc.architecture.vertx.core.components.PortContext
import io.github.jahrim.hexarc.architecture.vertx.core.dsl.VertxDSL.*
import io.github.jahrim.hexarc.persistence.bson.dsl.BsonDSL.{*, given}
import io.vertx.core.{Future, Promise, Vertx}
import org.bson.{BsonDocument, BsonTimestamp, BsonValue, Document}
import io.github.jahrim.chess.authentication.service.components.exceptions.*
import io.github.jahrim.hexarc.persistence.PersistentCollection
import io.github.jahrim.hexarc.persistence.mongodb.language.MongoDBQueryLanguage
import io.github.jahrim.hexarc.persistence.mongodb.language.queries.{
  CreateQuery,
  ReadQuery,
  UpdateQuery
}

import java.nio.charset.StandardCharsets
import java.sql.Timestamp
import java.time.temporal.ChronoUnit
import java.time.{Instant, ZonedDateTime}
import java.util.{Date, UUID}
import scala.util.{Failure, Random, Using}

class AuthenticationModel(users: PersistentCollection with MongoDBQueryLanguage)
    extends AuthenticationPort:
  override protected def init(context: PortContext): Unit = {}

  override def registerUser(
      username: String,
      email: String,
      passwordInput: String
  ): Future[String] =
    context.vertx.executeBlocking { promise =>
      context.log.info(s"Registering user $username...")
      val hashString = BCrypt.withDefaults.hashToString(10, passwordInput.toCharArray)
      val randomToken = UUID.randomUUID.toString
      users
        .create(
          CreateQuery(
            bson {
              "username" :: username
              "password" :: hashString
              "email" :: email
              "token" :# {
                "id" :: randomToken
                "expiration" :: Instant.now.plus(30, ChronoUnit.MINUTES)
              }
            }
          )
        )
        .fold(
          exception =>
            promise.fail(exception)
            context.log.info(s"Failed registering user $username.")
          ,
          success =>
            promise.complete(randomToken)
            context.log.info(s"User $username successfully registered.")
        )
    }
  override def loginUser(username: String, password: String): Future[String] =
    context.vertx.executeBlocking { promise =>
      context.log.info(s"Logging user $username...")
      users
        .read(
          ReadQuery(
            Filters.eq("username", username),
            Projections.include("password")
          )
        )
        .fold(
          exception => promise.fail(exception),
          userInfos =>
            if userInfos.nonEmpty then
              val passwordVerify = BCrypt.verifyer.verify(
                password.toCharArray,
                userInfos.head.require("password").as[String]
              )
              if passwordVerify.verified then
                val tokenId: String = UUID.randomUUID().toString
                users
                  .update(
                    UpdateQuery(
                      Filters.eq("username", username),
                      combine(
                        set("token.id", tokenId),
                        set(
                          "token.expiration",
                          Instant.now.plus(30, ChronoUnit.MINUTES)
                        )
                      )
                    )
                  )
                  .fold(
                    exception =>
                      promise.fail(exception)
                      context.log.info(s"Failed logging user $username.")
                    ,
                    success =>
                      promise.complete(tokenId)
                      context.log.info(s"User $username successfully logged in.")
                  )
              else promise.fail(IncorrectPasswordException())
            else promise.fail(UserNotFoundException())
        )
    }

  override def getUserInformation(username: String): Future[User] =
    context.vertx.executeBlocking { promise =>
      context.log.info(s"Getting user information $username...")
      users
        .read(
          ReadQuery(
            Filters.eq("username", username),
            Projections.include("username", "email")
          )
        )
        .fold(
          exception =>
            promise.fail(UserNotFoundException())
            context.log.info(s"Failed getting user $username information.")
          ,
          userInfos =>
            promise.complete(userInfos.head.as[User])
            context.log.info(s"User $username successfully got information.")
        )
    }

  override def updatePassword(username: String, password: String): Future[Unit] =
    context.vertx.executeBlocking { promise =>
      context.log.info(s"Updating password $username...")
      users
        .read(
          ReadQuery(
            Filters.eq("username", username),
            Projections.include("username", "password")
          )
        )
        .fold(
          exception => promise.fail(exception),
          userInfos =>
            if userInfos.nonEmpty then
              users
                .update(
                  UpdateQuery(
                    Filters.eq("username", username),
                    set("password", BCrypt.withDefaults.hashToString(10, password.toCharArray))
                  )
                )
                .fold(
                  exception =>
                    promise.fail(exception)
                    context.log.info("Failed updating password.")
                  ,
                  success =>
                    promise.complete()
                    context.log.info("Password successfully updated.")
                )
            else promise.fail(UserNotFoundException())
        )
    }

  override def validateToken(tokenId: String): Future[String] =
    context.vertx.executeBlocking { promise =>
      context.log.info("Validating token...")
      users
        .read(
          ReadQuery(
            Filters.eq("token.id", tokenId),
            Projections.include("username", "token.expiration")
          )
        )
        .fold(
          exception =>
            promise.fail(exception)
            context.log.info("Failed validating password.")
          ,
          userInfos =>
            if userInfos.nonEmpty then
              val date: Instant = userInfos.head.require("token.expiration").as[Instant]
              if Instant.now().isBefore(date) then
                promise.complete(userInfos.head.require("username").as[String])
              else promise.fail(ExpiredTokenException())
            else promise.fail(ExpiredTokenException())
        )

    }

  override def revokeToken(tokenId: String): Future[Unit] =
    context.vertx.executeBlocking { promise =>
      context.log.info("Revoking token...")
      users
        .update(
          UpdateQuery(
            Filters.eq("token.id", tokenId),
            unset("token.id")
          )
        )
        .fold(
          exception =>
            promise.fail(exception)
            context.log.info("Failed revoking token")
          ,
          success =>
            promise.complete()
            context.log.info("Token successfully revoked")
        )
    }
