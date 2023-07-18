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
//import io.github.jahrim.hexarc.persistence.bson.dsl.*
//
//import java.sql.Timestamp
//import java.time.Instant
//import java.time.temporal.ChronoUnit
//import java.util.Date
//import java.util.UUID
//import java.time.ZonedDateTime
//import scala.util.{Failure, Random, Using}
//import com.mongodb.client.model.Updates.*
//import io.github.jahrim.chess.authentication.service.components.exceptions.UserNotFoundException
//import io.github.jahrim.chess.authentication.service.components.adapters.http.AuthenticationHttpAdapter
//import io.github.jahrim.chess.authentication.service.main.Args
//import io.github.jahrim.hexarc.persistence.bson.PersistentDocumentCollection
//import io.github.jahrim.hexarc.persistence.mongodb.MongoDBPersistentCollection
//
//import java.nio.charset.StandardCharsets
//
//@main def loginUser(args: String*): Unit =
//  val arguments: Args = Args(args)
//
//  val users = MongoDBPersistentCollection(
//    connection = arguments.mongoDBConnection(),
//    database = arguments.mongoDBDatabase(),
//    collection = arguments.mongoDBCollection()
//  ).get
//
//  println("after")
//
//  users
//    .read(bson {
//      "username" :: "freddiemerc"
//    })
//    .fold(
//      e => println("failure"),
//      userInfos =>
//        println(userInfos)
//        val passwordVerify = BCrypt.verifyer.verify(
//          "passFreddie".toCharArray,
//          userInfos.head.require("password").as[String]
//        )
//        println(passwordVerify)
//        if passwordVerify.verified then
//          val tokenId: String = UUID.randomUUID().toString
//          users
//            .update(
//              Filters.eq("username", "freddiemerc"),
//              combine(
//                set("token.id", tokenId),
//                set("token.expiration", ZonedDateTime.now.plus(30, ChronoUnit.MINUTES).toInstant)
//              )
//            )
//            .fold(
//              error => println(error.getMessage),
//              success => println(tokenId)
//            )
//        else println("password is not verified!!!")
//    )
