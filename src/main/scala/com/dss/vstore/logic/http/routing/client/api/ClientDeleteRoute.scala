package com.dss.vstore.logic.http.routing.client.api

import akka.actor.ActorSystem
import akka.http.scaladsl.server.{Route, StandardRoute}
import akka.stream.ActorMaterializer
import com.dss.vstore.logic.models.ClientMeta
import com.dss.vstore.utils.ModulesChain
import akka.http.scaladsl.server.directives.MethodDirectives.delete
import akka.http.scaladsl.server.directives.BasicDirectives.extractRequest
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.http.scaladsl.server.RouteConcatenation._
import com.dss.vstore.utils.modules.ParsedConfig
import com.dss.vstore.utils.types.LoginHash

import scala.concurrent.ExecutionContext

class ClientDeleteRoute(client: ClientMeta, modules: ModulesChain) {

  implicit val materializer: ActorMaterializer = modules.materializer
  implicit val system: ActorSystem             = modules.system
  implicit val ec:     ExecutionContext        = modules.system.dispatcher
  implicit val config: ParsedConfig            = modules.config


  /**
    * Returns a [[Route]] that handles GET operations on objects
    */
  def deleteClient(login: LoginHash)(implicit modules: ModulesChain, ec: ExecutionContext): Route = {
//    Logger.debug(s"DELETE_CLIENT_BENCH $getCurrentLocalDateTimeStamp start deleteClient")
    delete {
      extractRequest { req =>
        archiveClient(login: LoginHash)
      }
    }
  }

  def archiveClient(login: LoginHash): Route = { complete("ARCHIVE CLIENT") }

}

object ClientDeleteRoute {

  def apply(client: ClientMeta)(implicit modules: ModulesChain): ClientDeleteRoute =
    new ClientDeleteRoute(client, modules)
}