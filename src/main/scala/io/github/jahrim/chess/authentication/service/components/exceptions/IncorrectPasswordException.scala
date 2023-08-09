package io.github.jahrim.chess.authentication.service.components.exceptions

/**
 * An [[AuthenticationServiceException]] triggered when a user attempts to
 * log in with an incorrect password.
 *
 * @param username the name of the user.
 */
class IncorrectPasswordException(username: String)
    extends AuthenticationServiceException(s"Incorrect password for user '$username'.")