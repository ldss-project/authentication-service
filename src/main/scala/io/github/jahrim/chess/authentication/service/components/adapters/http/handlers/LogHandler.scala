package io.github.jahrim.chess.authentication.service.components.adapters.http.handlers

import io.github.jahrim.chess.authentication.service.components.adapters.http.HttpHandler
import io.github.jahrim.chess.authentication.service.components.adapters.http.handlers.LogHandler.*
import io.vertx.ext.web.RoutingContext

import scala.jdk.CollectionConverters.{IterableHasAsScala, MapHasAsScala}

/**
 * An [[HttpHandler]] that logs all incoming requests through the specified [[Logger]].
 * @param logger the specified logger.
 * @param verbosityLevel the specified verbosity level (default: maximum verbosity).
 *                       <br/> - =0: disables logging.
 *                       <br/> - >0: logs the `date`, `request number` and `route` of the request.
 *                       <br/> - >1: logs the `body` of the request.
 *                       <br/> - >2: logs the `query parameters` of the request.
 */
case class LogHandler(
    private val logger: String => Unit,
    private val verbosityLevel: Int = Int.MaxValue
) extends HttpHandler:
  private var requestCount: Int = -1

  override def handle(c: RoutingContext): Unit =
    var logString = ""
    if this.verbosityLevel > 0 then
      this.requestCount = this.requestCount + 1
      logString =
        logString + s"\nRequest $requestCount:\n${c.request.method}: ${c.request.absoluteURI}"
      if this.verbosityLevel > 1 then
        logString =
          logString + s"\nWith body:\n${stringOf(Option(c.body).flatMap(b => Option(b.asString)))}"
      if this.verbosityLevel > 2 then
        logString = logString + s"\nWith query params:\n${stringOf(c.queryParams.asScala)}\n"
      logger(logString)
    c.next()

/** Companion object of [[LogHandler]]. */
object LogHandler:
  /**
   * @param iterable the specified [[Iterable]].
   * @return the string representation of the specified [[Iterable]].
   */
  private def stringOf(iterable: Iterable[?]): String =
    if iterable.isEmpty then "{}" else iterable.mkString(",")
