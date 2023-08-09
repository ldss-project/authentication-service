package io.github.jahrim.chess.authentication.service.components.exceptions

/**
 * An [[Exception]] triggered by the authentication service.
 *
 * @param message a detailed description of the [[Exception]].
 */
class AuthenticationServiceException(message: String) extends Exception(message)
