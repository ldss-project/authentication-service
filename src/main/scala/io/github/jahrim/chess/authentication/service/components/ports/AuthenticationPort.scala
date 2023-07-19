package io.github.jahrim.chess.authentication.service.components.ports

import io.github.jahrim.hexarc.architecture.vertx.core.components.Port
import io.github.jahrim.chess.authentication.service.components.data.*
import io.vertx.core.Future
trait AuthenticationPort extends Port:
  def registerUser(username: String, email: String, password: String): Future[String]
  def loginUser(username: String, password: String): Future[String]
  def getUserInformation(username: String): Future[User]
  def updatePassword(username: String, password: String): Future[Unit]
  def validateToken(tokenId: String): Future[String]
  def revokeToken(tokenId: String): Future[String]
