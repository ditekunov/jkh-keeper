package com.dss.vstore.logic.http.routing.client.api

import akka.actor.ActorSystem
import akka.http.scaladsl.server.{Route, StandardRoute}
import akka.stream.ActorMaterializer
import com.dss.vstore.logic.models.ClientMeta
import com.dss.vstore.utils.ModulesChain
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.BasicDirectives.extractRequest
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.http.scaladsl.server.RouteConcatenation._
import com.dss.vstore.utils.modules.ParsedConfig
import com.dss.vstore.utils.types.LoginHash

import scala.concurrent.ExecutionContext

class ClientGetRoute(client: ClientMeta, modules: ModulesChain) {

  implicit val materializer: ActorMaterializer = modules.materializer
  implicit val system: ActorSystem             = modules.system
  implicit val ec:     ExecutionContext        = modules.system.dispatcher
  implicit val config: ParsedConfig            = modules.config


  /**
    * Returns a [[Route]] that handles GET operations on objects
    */
  def getClient(login: LoginHash)(implicit modules: ModulesChain, ec: ExecutionContext): Route = {
//    Logger.debug(s"GET_CLIENT_BENCH $getCurrentLocalDateTimeStamp start getClient")
    get {
      extractRequest { req =>
        getMetersData(login: LoginHash) ~
          getDebt(login: LoginHash) ~
          getClientMeta(login: LoginHash)

      }
    }
  }

  def getMetersData(login: LoginHash): Route = {complete("GET METERS DATA")}

  def getDebt(login: LoginHash): Route = {complete("GET DEBT")}

  def getClientMeta(login: LoginHash): Route = {complete("GET CLIENT")}

}

object ClientGetRoute {

  def apply(client: ClientMeta)(implicit modules: ModulesChain): ClientGetRoute =
    new ClientGetRoute(client, modules)
}