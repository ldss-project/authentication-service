package io.github.jahrim.chess.authentication.service.util.extension

import io.vertx.core.Future

import scala.util.Try

/** An extension for [[Future VertxFuture]]s. */
object VertxFutureExtension:
  /**
   * Create a new [[Future VertxFuture]] containing the result of
   * the specified block of code.
   *
   * @param code the specified block of code.
   * @tparam T the type of the result returned by the specified block
   *           of code.
   * @return a new vertx [[Future VertxFuture]] containing the result
   *         of the specified block of code.
   *         The [[Future VertxFuture]] fails if the specified block
   *         of code throws an [[Exception]].
   */
  def future[T](code: => T): Future[T] =
    Future.future[T] { promise =>
      Try { code }.fold(e => promise.fail(e), r => promise.complete(r))
    }
