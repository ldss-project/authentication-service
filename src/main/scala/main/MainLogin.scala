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

@main def loginUser(args: String*): Unit =
  println(args.mkString(","))
  val connectionString: String = args.head
  println(connectionString)

  var users: Option[MongoCollection[BsonDocument]] = None
  System.setProperty("java.naming.provider.url", "dns://8.8.8.8")
  val serverApi = ServerApi
    .builder()
    .version(ServerApiVersion.V1)
    .build()

  val settings = MongoClientSettings
    .builder()
    .applyConnectionString(new ConnectionString(connectionString))
    .serverApi(serverApi)
    .build()
  println("before")

  val mongoClient = MongoClients.create(settings)
  val database = mongoClient.getDatabase("authentication")
  users = Some(database.getCollection("users", classOf[BsonDocument]))

  println("after")

  val document =
    Option(users.get.find(bson { "username" :: "freddiemerc" }).first()).getOrElse {
      throw UserNotFoundException()
    }
  println(document)
  println((document("password"): Option[BsonValue]).map(_.as[String]))
  val passwordVerify = BCrypt.verifyer.verify(
    "passFreddie".toCharArray,
    document.require("password").as[String]
  )

  println(passwordVerify)
