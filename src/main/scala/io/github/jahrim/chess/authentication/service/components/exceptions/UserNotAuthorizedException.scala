package io.github.jahrim.chess.authentication.service.components.exceptions

/**
 * An [[AuthenticationServiceException]] triggered when a user attempts to
 * access a sensitive operation in this service without authorization.
 */
class UserNotAuthorizedException
    extends AuthenticationServiceException("Unauthorized attempt to access a sensitive operation.")
