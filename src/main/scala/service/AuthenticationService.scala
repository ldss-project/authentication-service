package service

import at.favre.lib.crypto.bcrypt.BCrypt
import com.mongodb.client.{MongoClients, MongoCollection}
import com.mongodb.client.model.Filters
import com.mongodb.{ConnectionString, MongoClientSettings, ServerApi, ServerApiVersion}
import io.github.jahrim.hexarc.architecture.vertx.core.components.*
import io.jsonwebtoken.Jwts
import io.vertx.core.Future
import io.vertx.ext.web.Router
import org.bson.Document

import java.sql.Timestamp
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.{Date, UUID}
import scala.util.{Failure, Using}

object AuthenticationService:

  // ADAPTER
  class AuthenticationAdapterHttp extends Adapter[AuthenticationPort]:
    override protected def init(context: AdapterContext[AuthenticationPort]): Unit =
      val router = Router.router(context.vertx)

      router.get("/").handler { message =>
        context.log.info("Welcome!")
        message.response().send("Ciao, questo e' l'Authentication Service...")
      }
      router.post("/user/:username").handler { message =>
        context.api.registerUser("paolo", "paolo@gmail.com", "1234")
      }
      context.vertx
        .createHttpServer()
        .requestHandler(router)
        .listen(8080, server => context.log.info("The server is up!"))
