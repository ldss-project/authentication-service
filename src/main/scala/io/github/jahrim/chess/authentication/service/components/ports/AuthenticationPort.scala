package io.github.jahrim.chess.authentication.service.components.ports

import io.github.jahrim.chess.authentication.service.components.data.*
import io.github.jahrim.hexarc.architecture.vertx.core.components.Port
import io.vertx.core.Future

/**
 * A [[Port]] which handles the registration, login and logout of
 * the users in a service.
 */
trait AuthenticationPort extends Port:
  /**
   * Register a new user with the specified username, email and
   * password in the authentication service.
   *
   * @param username the specified username.
   * @param email    the specified email.
   * @param password the specified password.
   * @return a [[Future]] containing the session created for the user.
   *         The [[Future]] completes when the registration is completed
   *         successfully; otherwise it fails.
   */
  def registerUser(username: String, email: String, password: String): Future[UserSession]

  /**
   * Authenticate a user with the specified username and password in
   * the authentication service.
   *
   * @param username the specified username.
   * @param password the specified password.
   * @return a [[Future]] containing the session created for the user.
   *         The [[Future]] completes when the login is completed
   *         successfully; otherwise it fails.
   */
  def loginUser(username: String, password: String): Future[UserSession]

  /**
   * Get the profile information of the specified user in the authentication
   * service.
   *
   * @param username the name of the specified user.
   * @return a [[Future]] containing the [[User]] information of the user.
   *         The [[Future]] completes when the profile information is
   *         retrieved successfully; otherwise it fails.
   */
  def getUserInformation(username: String): Future[User]

  /**
   * Update the password of the specified user with the specified password
   * in the authentication service.
   *
   * @param username the name of the specified user.
   * @param password the specified password.
   * @return a [[Future]] that completes when the update is completed
   *         successfully; otherwise it fails.
   */
  def updatePassword(username: String, password: String): Future[Unit]

  /**
   * Validate the specified token in the authentication service,
   * verifying that it is owned by the specified user.
   *
   * @param token the specified token.
   * @param username the name of the specified user.
   * @return a [[Future]] that completes when the token validation is completed
   *         successfully; otherwise it fails.
   */
  def validateToken(token: Token, username: String): Future[Unit]

  /**
   * Revoke the specified token from the authentication service.
   *
   * @param token the specified token.
   * @return a [[Future]] that completes when the token revocation is completed
   *         successfully; otherwise it fails.
   */
  def revokeToken(token: Token): Future[Unit]
