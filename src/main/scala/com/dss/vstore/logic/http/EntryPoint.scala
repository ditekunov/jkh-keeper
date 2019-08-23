package com.dss.vstore.logic.http

import akka.actor.ActorSystem
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.dss.vstore.utils._
import akka.http.scaladsl.server.directives.HeaderDirectives.headerValueByName
import akka.http.scaladsl.server.Directives._
import com.dss.vstore.utils.StatsSupport.StatsHolder
import javax.net.ssl.SSLContext
import akka.http.scaladsl.{ConnectionContext, Http}
import com.dss.vstore.logic.{AccessDeniedError, InvalidAuthorizationStringError}
import com.dss.vstore.logic.http.directives.SecurityDirectives.checkRequester
import com.dss.vstore.logic.http.routing.{BankApiRoute, InternalApiRoute, ReceiverApiRoute}
import com.dss.vstore.utils.types.AuthorizationString.AuthorizationString
import com.dss.vstore.logic.http.routing.client.api.ClientApiRoute
import com.dss.vstore.utils.modules.Encrypter

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

class EntryPoint(val modules: ModulesChain) {
  implicit val ec:           ExecutionContext  = modules.system.dispatcher // Execution Context Executor
  implicit val system:       ActorSystem       = modules.system            // Handles ActorSystem behavior
  implicit val materializer: ActorMaterializer = ActorMaterializer()       // Handles Actors behavior
  implicit val encrypter:    Encrypter         = new Encrypter    // Handles encrypting/decrypting operation

  private var binding: Option[Future[ServerBinding]] = None

  val routes: Route =
    pathPrefix("vstore") {
      (headerValueByName("Requester") & headerValueByName("Signature") & headerValueByName("Authorization")) {
        (rawRequester, maybeSignature, auth) =>
          checkRequester(rawRequester) { requester: Requester =>
            requester match {
              case `Client` => AuthorizationString(auth) match {
                case Success(authorizationString) => ClientApiRoute()
                case Failure(_) =>
                  complete(InvalidAuthorizationStringError("Client authorization string is invalid"))
              }
              case `Bank` => AuthorizationString(auth) match {
                case Success(authorizationString) => BankApiRoute()
                case Failure(_) =>
                  complete(InvalidAuthorizationStringError("Bank authorization string is invalid"))
              }
              case `Receiver` => AuthorizationString(auth) match {
                case Success(authorizationString) => ReceiverApiRoute()
                case Failure(_) =>
                  complete(InvalidAuthorizationStringError("Receiver authorization string is invalid"))
              }
              case `Admin` => AuthorizationString(auth) match {
                case Success(authorizationString) => InternalApiRoute()
                case Failure(_) =>
                  complete(InvalidAuthorizationStringError("Admin authorization string is invalid"))
              }
              case _ => complete(AccessDeniedError("Invalid Client-Entity"))
            }
          }
      }
    } ~
      pathPrefix("vmanage") {

    }

  def main(sslContext: Option[SSLContext]): Try[StatsHolder] =
    Try({
      modules.system.log.info(s"Tables created")
      val statsHolder = new StatsHolder

      val endpointConf = modules.config.mainBlock.endpointConfig

      sslContext match {

        case Some(ssl) =>
          this.binding = Some(
            Http().bindAndHandle(routes,
              endpointConf.host,
              endpointConf.port,
              connectionContext = ConnectionContext.https(ssl))
          )
        case None => this.binding = Some(Http().bindAndHandle(routes, endpointConf.host, endpointConf.port))
      }

      println(s"Server online at http://${endpointConf.host}:${endpointConf.port}\nPress RETURN to stop...")
      statsHolder
    })

  def stop(): Unit = {
    unbind(this.binding)
  }

  def unbind(binding: Option[Future[ServerBinding]]): Unit = {
    binding match {
      case Some(bindingUnwrapped) =>
        bindingUnwrapped
          .flatMap(_.unbind())
          .onComplete({ _ =>
            modules.system
              .terminate()
              .map({ _ =>
                modules.dbConnectionClose
              })
          })
      case None =>
        modules.system
          .terminate()
          .map({ _ =>
            modules.dbConnectionClose
          })
    }
  }
}
