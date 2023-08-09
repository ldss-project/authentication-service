package io.github.jahrim.chess.authentication.service.components.data

/**
 * A session of a [[User]] authenticated in this service.
 *
 * @param username the name of the [[User]].
 * @param token the [[Token]] given to the [[User]] upon authentication.
 */
case class UserSession(username: String, token: Token)
