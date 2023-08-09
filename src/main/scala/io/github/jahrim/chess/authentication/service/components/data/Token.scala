package io.github.jahrim.chess.authentication.service.components.data

import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

/**
 * A permit for accessing sensitive operations in this service.
 *
 * @param id the identifier of this [[Token]].
 */
case class Token(
    id: String = UUID.randomUUID.toString,
    expiration: Instant = Instant.now.plus(30, ChronoUnit.MINUTES)
)
