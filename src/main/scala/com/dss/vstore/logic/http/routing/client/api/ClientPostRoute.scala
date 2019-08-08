package com.dss.vstore.logic.http.routing.client.api

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.dss.vstore.logic.models.ClientMeta
import com.dss.vstore.utils.ModulesChain
import akka.http.scaladsl.server.directives.MethodDirectives.post
import akka.http.scaladsl.server.directives.BasicDirectives.extractRequest
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.http.scaladsl.server.RouteConcatenation._
import com.dss.vstore.utils.modules.ParsedConfig
import com.dss.vstore.utils.types.LoginHash

import scala.concurrent.ExecutionContext

class ClientPostRoute(client: ClientMeta, modules: ModulesChain) {

  implicit val materializer: ActorMaterializer = modules.materializer
  implicit val system: ActorSystem             = modules.system
  implicit val ec:     ExecutionContext        = modules.system.dispatcher
  implicit val config: ParsedConfig            = modules.config


  /**
    * Returns a [[Route]] that handles GET operations on objects
    */
  def postClient(login: LoginHash)(implicit modules: ModulesChain, ec: ExecutionContext): Route = {
//    Logger.debug(s"POST_CLIENT_BENCH $getCurrentLocalDateTimeStamp start postClient")
    post {
      complete("POST ROUTE")
    }
  }
}

object ClientPostRoute {

  def apply(client: ClientMeta)(implicit modules: ModulesChain): ClientPostRoute =
    new ClientPostRoute(client, modules)
}