package io.github.jahrim.chess.authentication.service.components.exceptions

/**
 * An [[AuthenticationServiceException]] triggered when a user is queried
 * but not found.
 *
 * @param username the name of the user.
 */
class UserNotFoundException(username: String)
    extends AuthenticationServiceException(s"User '$username' not found.")
