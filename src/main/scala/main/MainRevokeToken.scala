//package main
//
//import at.favre.lib.crypto.bcrypt.BCrypt
//import com.mongodb.client.MongoCollection
//import io.github.jahrim.hexarc.architecture.vertx.core.dsl.VertxDSL.*
//import io.vertx.core.{Future, Vertx}
//import com.mongodb.{ConnectionString, MongoClientSettings, ServerApi, ServerApiVersion}
//import com.mongodb.client.model.Filters
//import com.mongodb.client.MongoClients
//import org.bson.{BsonDocument, BsonTimestamp, BsonValue}
//import io.github.jahrim.hexarc.persistence.bson.dsl.BsonDSL.{*, given}
//
//import java.sql.Timestamp
//import java.time.{Instant, ZonedDateTime}
//import java.time.temporal.ChronoUnit
//import java.util.Date
//import java.util.UUID
//import scala.util.{Failure, Random, Using}
//import com.mongodb.client.model.Updates.*
//import io.github.jahrim.chess.authentication.service.components.exceptions.UserNotFoundException
//import io.github.jahrim.chess.authentication.service.components.adapters.http.AuthenticationHttpAdapter
//import io.github.jahrim.chess.authentication.service.main.Args
//import io.github.jahrim.hexarc.persistence.mongodb.MongoDBPersistentCollection
//
//import java.nio.charset.StandardCharsets
//
//@main def revokeUserToken(args: String*): Unit =
//  val arguments: Args = Args(args)
//  val users = MongoDBPersistentCollection(
//    connection = arguments.mongoDBConnection(),
//    database = arguments.mongoDBDatabase(),
//    collection = arguments.mongoDBCollection()
//  ).get
//
//  println("after")
//
//  users
//    .read(Filters.eq("token.id", "c8a92ba4-d1ff-477e-99e2-9d830ab989d1"))
//    .fold(
//      exception => exception.getMessage,
//      userInfos =>
//        println(userInfos.head)
//        println(userInfos.head.require("username").as[String])
//        println(userInfos.head.require("_id").asObjectId.getValue)
//        val date: ZonedDateTime = userInfos.head.require("token.expiration")
//        if ZonedDateTime.now().isBefore(date) then {
//          println("token is not expired")
//          println(userInfos.head.require("username").as[String])
//        } else
//          println("token expired!")
//          users.update(
//            Filters.eq("token.id", "c8a92ba4-d1ff-477e-99e2-9d830ab989d1"),
//            combine(
//              unset("token.id")
//            )
//          )
//          println(userInfos.head.require("username").as[String])
//    )
