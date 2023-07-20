package io.github.jahrim.chess.authentication.service.components.ports

import io.github.jahrim.hexarc.architecture.vertx.core.components.Port
import io.github.jahrim.chess.authentication.service.components.data.*
import io.vertx.core.Future

/**
 * A [[Port]] which handles the registration, login and logout of
 * the users in a service.
 */
trait AuthenticationPort extends Port:
  /**
   * Register a new user with the specified username, email and
   * password in the authentication service.
   * @param username the specified username.
   * @param email the specified email.
   * @param password the specified password.
   * @return a [[Future]] containing the token id created for the user.
   *         The [[Future]] completes when the registration is completed
   *         successfully; otherwise it fails.
   */
  def registerUser(username: String, email: String, password: String): Future[String]
  def loginUser(username: String, password: String): Future[String]
  def getUserInformation(username: String): Future[User]
  def updatePassword(username: String, password: String): Future[Unit]
  def validateToken(tokenId: String): Future[String]
  def revokeToken(tokenId: String): Future[Unit]
