package com.dss.vstore.logic.http

import akka.actor.ActorSystem
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model.{ContentType, HttpCharsets, HttpEntity, HttpResponse, MediaTypes, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.dss.vstore.utils._
import akka.http.scaladsl.server.directives.HeaderDirectives.headerValueByName
import akka.http.scaladsl.server.Directives._
import com.dss.vstore.utils.StatsSupport.StatsHolder
import javax.net.ssl.SSLContext
import akka.http.scaladsl.{ConnectionContext, Http}
import com.dss.vstore.logic.{AccessDeniedError, InvalidAuthorizationStringError, InvalidRequestError, SignatureDoesNotMatchError}
import com.dss.vstore.logic.http.directives.SecurityDirectives.checkRequester
import com.dss.vstore.logic.http.routing.{BankApiRoute, InternalApiRoute, ReceiverApiRoute, ServiceRoute}
import com.dss.vstore.utils.types.AuthorizationString.AuthorizationString
import com.dss.vstore.logic.http.routing.client.api.ClientApiRoute
import com.dss.vstore.logic.http.routing.client.api.ClientApiRoute.ClientApiRoutes
import com.dss.vstore.logic.models.ClientMeta
import com.dss.vstore.utils.modules.Encrypter
import com.dss.vstore.utils.types.Signature.Signature
import com.dss.vstore.utils.converters.TypesHelper._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

class EntryPoint(val modules: ModulesChain) {
  implicit val ec: ExecutionContext = modules.system.dispatcher // Execution Context Executor
  implicit val system: ActorSystem = modules.system // Handles ActorSystem behavior
  implicit val materializer: ActorMaterializer = ActorMaterializer() // Handles Actors behavior
  implicit val encrypter: Encrypter = new Encrypter // Handles encrypting/decrypting operation

  private var binding: Option[Future[ServerBinding]] = None

  /**
    * Route, that handles Client API operations
    */
  val clientApiRoute = new ClientApiRoutes(Client)(modules: ModulesChain)

  val routes: Route =
    pathPrefix("vstore") {
      (headerValueByName("Requester") & headerValueByName("Signature") & headerValueByName("Authorization")) {
        (rawRequester, maybeSignature, auth) =>
          checkRequester(rawRequester) {
            case client: ClientMeta => AuthorizationString(auth) match {
              case Success(authorizationString) => clientApiRoute(client, authorizationString)(modules)
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
    } ~ pathPrefix("vmanage") {
      pathPrefix(Remaining) {
        case remain if remain == "healthcheck" =>
          complete(HttpResponse(
            StatusCodes.OK,
            entity = HttpEntity(ContentType.WithCharset(MediaTypes.`text/plain`, HttpCharsets.`UTF-8`), "OK"))
          )

        case remain if remain == "deep_check" =>
          (headerValueByName("Client-Entity")
            & headerValueByName("Signature")
            & headerValueByName("Authorization")) {
            (rawRequester, maybeSignature, auth) =>
              toSignature(maybeSignature) match {
                case signature =>

                  checkHealthcheckEntity(rawRequester)(modules) { requester: Requester =>
                    checkHealthCheckSignature(requester, signature)(ec, modules, encrypter) {

                      val healthcheckUri = modules.config.misc.healthcheckUrl
                      HealthCheckRoute(healthcheckUri)(ec, modules, encrypter, system)
                    }
                  }
                case _ => complete(SignatureDoesNotMatchError("Signature does not match"))
              }
          }
        case remain if remain == "service" =>
          (headerValueByName("Client-Entity") & headerValueByName("Signature") & headerValueByName("Authorization")) {
            (rawRequester, signature, auth) =>
              if (rawRequester == "admin") AuthorizationString(auth) match {
                case Success(authorizationString) =>
                  checkAdminAcl(authorizationString)(ec, modules, encrypter) {
                    ServiceRoute()
                  }
                case Failure(_) => complete(InvalidAuthorizationStringError("Counter authorization string is invalid"))
              }
              else complete(EntityDoesNotMatchError("Only admin have access to ServiceRoute"))
          }
        case remain => complete(InvalidRequestError("Invalid url was provided"))
      }

      def main() =
        Try {
          modules.system.log.info(s"Tables created")
          val statsHolder = new StatsHolder
          val endpointConf = modules.config.mainBlock.endpointConfig
          println(s"Server online at http://${endpointConf.host}:${endpointConf.port}\nPress RETURN to stop...")
        }

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
}
