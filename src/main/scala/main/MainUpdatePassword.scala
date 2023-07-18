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
//import java.time.Instant
//import java.time.temporal.ChronoUnit
//import java.util.Date
//import java.util.UUID
//import scala.util.{Failure, Random, Using}
//import com.mongodb.client.model.Updates.*
//import io.github.jahrim.chess.authentication.service.components.exceptions.UserNotFoundException
//import io.github.jahrim.chess.authentication.service.service.AuthenticationService.AuthenticationAdapterHttp
//import io.github.jahrim.chess.authentication.service.main.Args
//
//import java.nio.charset.StandardCharsets
//
//@main def updateUserPassword(args: String*): Unit =
//  val arguments: Args = Args(args)
//  val connectionString: String = arguments.mongoDBConnection()
//
//  var users: Option[MongoCollection[BsonDocument]] = None
//  System.setProperty("java.naming.provider.url", "dns://8.8.8.8")
//  val serverApi = ServerApi
//    .builder()
//    .version(ServerApiVersion.V1)
//    .build()
//
//  val settings = MongoClientSettings
//    .builder()
//    .applyConnectionString(new ConnectionString(connectionString))
//    .serverApi(serverApi)
//    .build()
//  println("before")
//
//  val mongoClient = MongoClients.create(settings)
//  val database = mongoClient.getDatabase("authentication")
//  users = Some(database.getCollection("users", classOf[BsonDocument]))
//
//  println("after")
//
//  val hash = BCrypt.withDefaults.hashToString(10, "passFreddie1".toCharArray)
//  println(hash)
//
//  Option(
//    users.get.updateOne(
//      Filters.eq("username", "freddiemerc"),
//      combine(
//        set("password", hash)
//      )
//    )
//  ).filter(_.getMatchedCount > 0).getOrElse { throw UserNotFoundException() }
