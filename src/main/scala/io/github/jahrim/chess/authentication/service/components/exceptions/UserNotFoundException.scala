package io.github.jahrim.chess.authentication.service.components.exceptions


/**
 * [[Exception]] thrown when a user is not found n the database.
 */
class UserNotFoundException extends IllegalArgumentException("User not found in the database!")
