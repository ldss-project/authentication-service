package io.github.jahrim.chess.authentication.service.components.exceptions

/**
 * An [[AuthenticationServiceException]] triggered when a user attempts to
 * sign in to the service with a username that is already taken.
 *
 * @param username the name of the user.
 */
class UsernameAlreadyTakenException(username: String)
    extends AuthenticationServiceException(s"User '$username' already taken.")
