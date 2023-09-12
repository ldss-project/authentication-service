/*
MIT License

Copyright (c) 2023 Cesario Jahrim Gabriele, Kentpayeva Madina

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
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
      name = "AuthenticationService"

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
