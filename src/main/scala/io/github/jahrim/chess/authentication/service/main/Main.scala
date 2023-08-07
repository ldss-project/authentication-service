package io.github.jahrim.chess.authentication.service.main

import io.github.jahrim.chess.authentication.service.components.adapters.http.AuthenticationHttpAdapter
import io.github.jahrim.chess.authentication.service.components.ports.{
  AuthenticationModel,
  AuthenticationPort
}
import io.github.jahrim.hexarc.architecture.vertx.core.dsl.VertxDSL.*
import io.github.jahrim.hexarc.persistence.mongodb.MongoDBPersistentCollection
import io.vertx.core.Vertx
import io.vertx.core.http.HttpServerOptions
import org.rogach.scallop.*

/** Main of the application. */
@main def main(args: String*): Unit =
  val arguments: Args = Args(args)

  DeploymentGroup.deploySingle(Vertx.vertx()) {
    new Service:
      name = "Authentication service"

      new Port[AuthenticationPort]:
        model = AuthenticationModel(
          users = MongoDBPersistentCollection(
            connection = arguments.mongoDBConnection(),
            database = arguments.mongoDBDatabase(),
            collection = arguments.mongoDBCollection()
          ).get
        )

        new Adapter(
          adapter = AuthenticationHttpAdapter(
            httpOptions = HttpServerOptions()
              .setHost(arguments.httpHost())
              .setPort(arguments.httpPort()),
            allowedOrigins = arguments.allowedOrigins()
          )
        )
  }

/**
 * The parsed command line arguments accepted by this application.
 *
 * @param arguments the sequence of command line arguments to parse.
 *
 * @see [[https://github.com/scallop/scallop Scallop Documentation on Github]].
 */
class Args(private val arguments: Seq[String]) extends ScallopConf(arguments):
  val httpHost: ScallopOption[String] = opt[String](
    name = "http-host",
    descr = "The server host for the http adapter of this service.",
    default = Some("localhost"),
    required = false
  )
  val httpPort: ScallopOption[Int] = opt[Int](
    name = "http-port",
    descr = "The server port for the http adapter of this service.",
    default = Some(8080),
    required = false
  )
  val allowedOrigins: ScallopOption[Seq[String]] = opt[String](
    name = "allowed-origins",
    descr = "A list of colon separated origins that are allowed to access this service.",
    default = Some(""),
    required = false
  ).map(_.split(";").toSeq)
  val mongoDBConnection: ScallopOption[String] = opt[String](
    name = "mongodb-connection",
    descr = "The connection string to the mongodb instance used by this service.",
    required = true
  )
  val mongoDBDatabase: ScallopOption[String] = opt[String](
    name = "mongodb-database",
    descr = "The database within the mongodb instance used by this service.",
    default = Some("authentication"),
    required = false
  )
  val mongoDBCollection: ScallopOption[String] = opt[String](
    name = "mongodb-collection",
    descr = "The collection within the mongodb database used by this service.",
    default = Some("users"),
    required = false
  )
  verify()
