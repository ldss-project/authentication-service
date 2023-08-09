package io.github.jahrim.chess.authentication.service.components.exceptions

/**
 * An [[AuthenticationServiceException]] triggered when the user attempts to
 * call the api of this service with invalid input parameters.
 *
 * @param message a detailed description of the [[Exception]].
 */
class MalformedInputException(message: String) extends AuthenticationServiceException(message)
