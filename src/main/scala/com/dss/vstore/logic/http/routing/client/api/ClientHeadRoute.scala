package com.dss.vstore.logic.http.routing.client.api

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.dss.vstore.logic.models.ClientMeta
import com.dss.vstore.utils.ModulesChain
import akka.http.scaladsl.server.directives.MethodDirectives.head
import akka.http.scaladsl.server.directives.FutureDirectives.onComplete
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.http.scaladsl.server.RouteConcatenation._
import com.dss.vstore.logic.NoSuchClientError
import com.dss.vstore.utils.modules.ParsedConfig
import com.dss.vstore.utils.types.Login.Login

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class ClientHeadRoute(client: ClientMeta, modules: ModulesChain) {

  implicit val materializer: ActorMaterializer = modules.materializer
  implicit val system:       ActorSystem       = modules.system
  implicit val ec:           ExecutionContext  = modules.system.dispatcher
  implicit val config:       ParsedConfig      = modules.config

  /**
    * Returns a [[Route]] that handles GET operations on objects
    */
  def headClient(login: Login)(implicit modules: ModulesChain, ec: ExecutionContext): Route = {
//    Logger.debug(s"HEAD_CLIENT_BENCH $getCurrentLocalDateTimeStamp start headClient")
    head {
      complete("HEAD ROUTE")
//      exists(loginHash)
    }
  }
//
//  /**
//    * Checks, whether client exists
//    */
//  def exists(login: LoginHash)(implicit modules: ModulesChain, ec: ExecutionContext): Route =
//    onComplete(modules.clientDal.getClientByLoginHash(login)) {
//      case Success(clients) if clients.nonEmpty => complete("Client with such login exists")
//      case Failure(_) => complete(NoSuchClientError(""))
//    }
}

object ClientHeadRoute {

  def apply(client: ClientMeta)(implicit modules: ModulesChain): ClientHeadRoute =
    new ClientHeadRoute(client, modules)
}