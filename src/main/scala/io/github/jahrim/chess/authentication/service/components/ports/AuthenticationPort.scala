/*
MIT License

Copyright (c) 2023 Cesario Jahrim Gabriele, Kentpayeva Madina

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
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
