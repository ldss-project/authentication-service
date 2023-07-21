package io.github.jahrim.chess.authentication.service.components.exceptions

/** [[Exception]] thrown when a user or a password are not found in the database. */
class MalformedInputException extends Exception("User or password are not found in the database!")
