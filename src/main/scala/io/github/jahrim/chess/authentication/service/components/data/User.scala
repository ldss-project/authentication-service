package io.github.jahrim.chess.authentication.service.components.data

/**
 * A user of the authentication service with the specified username and email.
 * @param username the specified username.
 * @param email the specified email.
 */
case class User(username: String, email: String)
