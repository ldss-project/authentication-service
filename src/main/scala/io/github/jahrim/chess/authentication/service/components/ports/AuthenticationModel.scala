package io.github.jahrim.chess.authentication.service.components.ports

import com.mongodb.client.model.{Filters, Projections, Updates}
import io.github.jahrim.chess.authentication.service.components.data.*
import io.github.jahrim.chess.authentication.service.components.data.codecs.Codecs.{*, given}
import io.github.jahrim.chess.authentication.service.components.exceptions.*
import io.github.jahrim.hexarc.architecture.vertx.core.components.PortContext
import io.github.jahrim.hexarc.architecture.vertx.core.dsl.VertxDSL.*
import io.github.jahrim.hexarc.persistence.PersistentCollection
import io.github.jahrim.hexarc.persistence.bson.dsl.BsonDSL.{*, given}
import io.github.jahrim.hexarc.persistence.mongodb.language.MongoDBQueryLanguage
import io.github.jahrim.hexarc.persistence.mongodb.language.queries.{
  CreateQuery,
  ReadQuery,
  UpdateQuery
}
import io.vertx.core.Future
import org.mindrot.jbcrypt.BCrypt
import java.time.Instant

/**
 * Business logic of an [[AuthenticationPort]].
 *
 * @param users the [[PersistentCollection]] used by the business logic to
 *              store the users' information.
 */
class AuthenticationModel(users: PersistentCollection with MongoDBQueryLanguage)
    extends AuthenticationPort:
  override protected def init(context: PortContext): Unit = {}

  override def registerUser(
      username: String,
      email: String,
      password: String
  ): Future[UserSession] =
    context.vertx.executeBlocking { promise =>
      context.log.info(s"Registering user $username...")
      users
        .read(
          ReadQuery(
            Filters.eq("username", username),
            Projections.include("username")
          )
        )
        .fold(
          exception =>
            promise.fail(exception)
            context.log.info(s"Failed registering user $username.")
          ,
          matches =>
            if matches.isEmpty then
              val hashString = BCrypt.hashpw(password, BCrypt.gensalt())
              val token = Token()
              users
                .create(
                  CreateQuery(
                    bson {
                      "username" :: username
                      "password" :: hashString
                      "email" :: email
                      "token" :: token
                    }
                  )
                )
                .fold(
                  exception =>
                    promise.fail(exception)
                    context.log.info(s"Failed registering user $username.")
                  ,
                  success =>
                    promise.complete(UserSession(username, token))
                    context.log.info(s"User $username successfully registered.")
                )
            else promise.fail(UsernameAlreadyTakenException(username))
        )
    }

  override def loginUser(username: String, password: String): Future[UserSession] =
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
              if BCrypt.checkpw(password, userInfos.head.require("password").as[String]) then
                val token = Token()
                users
                  .update(
                    UpdateQuery(
                      Filters.eq("username", username),
                      Updates.set("token", token.asBson)
                    )
                  )
                  .fold(
                    exception =>
                      promise.fail(exception)
                      context.log.info(s"Failed logging user $username.")
                    ,
                    success =>
                      promise.complete(UserSession(username, token))
                      context.log.info(s"User $username successfully logged in.")
                  )
              else promise.fail(IncorrectPasswordException(username))
            else promise.fail(UserNotFoundException(username))
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
            promise.fail(UserNotFoundException(username))
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
                    Updates.set("password", BCrypt.hashpw(password, BCrypt.gensalt()))
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
            else promise.fail(UserNotFoundException(username))
        )
    }

  def validateToken(token: Token, username: String): Future[Unit] =
    context.vertx.executeBlocking { promise =>
      context.log.info(s"Validating token $token against user $username...")
      users
        .read(
          ReadQuery(
            Filters.eq("token.id", token.id),
            Projections.include("username", "token")
          )
        )
        .fold(
          exception =>
            promise.fail(exception)
            context.log.info("Failed validating password.")
          ,
          userSessions =>
            if userSessions.nonEmpty then
              val userSession: UserSession = userSessions.head.as[UserSession]
              if Instant.now().isBefore(userSession.token.expiration) then
                if userSession.username == username then promise.complete()
                else promise.fail(UserNotAuthorizedException())
              else promise.fail(TokenExpiredException())
            else promise.fail(TokenExpiredException())
        )
    }

  override def revokeToken(token: Token): Future[Unit] =
    context.vertx.executeBlocking { promise =>
      context.log.info("Revoking token...")
      users
        .update(
          UpdateQuery(
            Filters.eq("token.id", token.id),
            Updates.unset("token.id")
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
