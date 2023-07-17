package service
import service.AuthenticationPort.*
import io.vertx.core.Future
import service.AuthenticationPort
import io.github.jahrim.hexarc.architecture.vertx.core.components.Port
trait AuthenticationPort extends Port:
  def registerUser(username: String, email: String, password: String): Future[String]
  def loginUser(username: String, password: String): Future[String]
  def getUserInformation(username: String): Future[User]
  def updatePassword(username: String, password: String): Future[Unit]
  def validateToken(tokenId: String): Future[String]
  def revokeToken(tokenId: String): Future[String]

object AuthenticationPort:
  case class User(username: String, email: String)