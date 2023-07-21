package io.github.jahrim.chess.authentication.service.components.exceptions

/**
 * [[Exception]] thrown when an authentication token is expired.
 */
class ExpiredTokenException extends Exception("Authentication token is expired!")
