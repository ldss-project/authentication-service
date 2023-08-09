package io.github.jahrim.chess.authentication.service.components.exceptions

/**
 * An [[AuthenticationServiceException]] triggered when a user attempts to
 * validate an expired token.
 */
class TokenExpiredException extends AuthenticationServiceException("Token expired.")
