//package service
//
//import com.mongodb.client.MongoCollection
//import at.favre.lib.crypto.bcrypt.BCrypt
//import io.vertx.core.Future
//import org.bson.{BsonTimestamp, Document}
//import com.mongodb.{ConnectionString, MongoClientSettings, ServerApi, ServerApiVersion}
//import com.mongodb.client.model.Filters
//import com.mongodb.client.MongoClients
//import io.github.jahrim.hexarc.architecture.vertx.core.components.PortContext
//import java.sql.Timestamp
//import java.util.Date
//import java.util.UUID
//import java.time.Instant
//import java.time.temporal.ChronoUnit
class AuthenticationModel //extends AuthenticationPort:
//  private var users: Option[MongoCollection[Document]] = None
//  override protected def init(context: PortContext): Unit =
//    System.setProperty("java.naming.provider.url", "dns://8.8.8.8")
//    val serverApi = ServerApi
//      .builder()
//      .version(ServerApiVersion.V1)
//      .build()
//    val settings = MongoClientSettings
//      .builder()
//      .applyConnectionString(new ConnectionString(connectionString))
//      .serverApi(serverApi)
//      .build()
//    // this.databaseUsers = Map.empty
//    println("before")
//    val mongoClient = MongoClients.create(settings)
//    val database = mongoClient.getDatabase("authentication")
//    this.users = Some(database.getCollection("users"))
//    println("after")
//
//  override def registerUser(
//      username: String,
//      email: String,
//      passwordInput: String
//  ): Future[String] =
//    context.vertx.executeBlocking { promise =>
//      val secureRandom: SecureRandom = new SecureRandom
//      val saltArray: Array[Byte] = new Array[Byte](16)
//      secureRandom.nextBytes(saltArray)
//      println(saltArray.mkString("Array(", ", ", ")"))
//
//      val bcryptHashData =
//        BCrypt.withDefaults().hashRaw(6, saltArray, "pFranco".getBytes(StandardCharsets.UTF_8))
//      println(bcryptHashData.rawHash.mkString("Array(", ", ", ")"))
//      println(bcryptHashData)
//      println(bcryptHashData.rawSalt.mkString("Array(", ", ", ")"))
//
//      val randomToken = UUID.randomUUID().toString
//      println(s"randomToken: $randomToken")
//      val password = Document("hash", bcryptHashString).append("salt", saltArray)
//      val token = Document("id", randomToken).append(
//        "expiration",
//        Timestamp.from(Instant.now().plus(5, ChronoUnit.MINUTES))
//      )
//      val user = Document("username", username)
//        .append("email", email)
//        .append("password", password)
//        .append("token", token)
//      println("fine")
//      this.users.get.insertOne(user)
//      promise.complete(randomToken)
//    }
//
//  /* if (databaseUsers.contains(username)) {
//    Option.empty
//  } else {
//    val user = User(username, email, password)
//    databaseUsers.put(username, user)
//    Option(user)
//  }*/
//
//  override def loginUser(username: String, password: String): Future[String] =
//    context.vertx.executeBlocking { promise =>
//      val document =
//        Option(
//          this.users.get
//            .find(bson {
//              "username" :: "francopersi"
//            })
//            .first()
//        ).getOrElse {
//          throw UserNotFoundException()
//        }
//      println(document)
//      println(document.require("password.hash").as[String])
//      val passwordVerification =
//        BCrypt
//          .verifyer()
//          .verify(
//            "pFranco".getBytes(StandardCharsets.UTF_8),
//            "$104496ba80093fdac96f45bb5bc7632cbbe145c337b104".getBytes(StandardCharsets.UTF_8)
//          )
//      promise.complete(retrieveToken.toString)
//    }
//
//  override def getUserInformation(username: String): Future[AuthenticationPort.User] = ???
//
//  override def updatePassword(username: String, password: String): Future[Unit] = ???
//
//  override def validateToken(tokenId: String): Future[String] = ???
//
//  override def revokeToken(tokenId: String): Future[String] = ???
//object AuthenticationModel:
//
//  private case class User(email: String, password: String)
