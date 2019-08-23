package com.dss.vstore.logic.http.routing.client.api

import akka.actor.ActorSystem
import akka.http.scaladsl.coding.{Deflate, Gzip, NoCoding}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.RouteConcatenation._
import akka.http.scaladsl.server.directives.CodingDirectives.encodeResponseWith
import akka.stream.ActorMaterializer
import com.dss.vstore.logic.models.ClientMeta
import com.dss.vstore.utils.modules.{Encrypter, ParsedConfig}
import com.dss.vstore.utils.types.AuthorizationString.AuthorizationString
import com.dss.vstore.utils.{ModulesChain, Requester}

import scala.concurrent.ExecutionContext

object ClientApiRoute {

  class ClientApiRoutes(client: Requester)(implicit modules: ModulesChain) {

    implicit val materializer: ActorMaterializer = modules.materializer
    implicit val system: ActorSystem             = modules.system
    implicit val ec: ExecutionContext            = modules.system.dispatcher
    implicit val config: ParsedConfig            = modules.config
    implicit val encrypter: Encrypter            = modules.encrypter

    /**
      * Returns a [[Route]] which handles bucket operations
      */
    def apply(client: ClientMeta, auth: AuthorizationString)(implicit modules: ModulesChain): Route = {
      lazy val loginHash = encrypter.getLogin(auth)
      lazy val passwordHash = encrypter.getPassword(auth)

      def getClient: Route = ClientGetRoute(client).getClient(loginHash)

      def headClient: Route = ClientHeadRoute(client).headClient(loginHash)

      def putClient: Route = ClientPutRoute(client).putClient(loginHash, passwordHash)

      def postClient: Route = ClientPostRoute(client).postClient(loginHash)

      def deleteClient: Route = ClientDeleteRoute(client).deleteClient(loginHash)

      encodeResponseWith(NoCoding, Gzip, Deflate) {
        getClient ~ headClient ~ putClient ~ postClient ~ deleteClient
      }
    }
  }

}
