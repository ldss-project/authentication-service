package main

import at.favre.lib.crypto.bcrypt.BCrypt
import com.mongodb.client.MongoCollection
import io.github.jahrim.hexarc.architecture.vertx.core.dsl.VertxDSL.*
import io.vertx.core.{Future, Vertx}
import com.mongodb.{ConnectionString, MongoClientSettings, ServerApi, ServerApiVersion}
import com.mongodb.client.model.Filters
import com.mongodb.client.MongoClients
import io.jsonwebtoken.Jwts
import org.bson.{BsonDocument, BsonTimestamp, BsonValue}
import io.github.jahrim.hexarc.persistence.bson.dsl.BsonDSL.{*, given}

import java.sql.Timestamp
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.UUID
import scala.util.{Failure, Random, Using}
import com.mongodb.client.model.Updates.*
import service.*
import service.AuthenticationService.AuthenticationAdapterHttp
import exception.UserNotFoundException

import java.nio.charset.StandardCharsets
import java.security.SecureRandom

/** Main class of the application. */
@main def main(args: String*): Unit =
  println(args.mkString(","))
  val connectionString: String = args.head
  println(connectionString)

  // println("Hello world!")
  /*  DeploymentGroup.deploySingle(Vertx.vertx()) {
    new Service:
      name = "Authentication service"

      new Port[AuthenticationPort]:
        model = AuthenticationModel()

        new Adapter(AuthenticationAdapterHttp())
  }*/
