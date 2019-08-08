package com.dss.vstore.logic.http.routing.client.api

import akka.actor.ActorSystem
import akka.http.scaladsl.coding.{Deflate, Gzip, NoCoding}
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.dss.vstore.utils.{ModulesChain, Requester}
import akka.http.scaladsl.server.directives.CodingDirectives.encodeResponseWith
import akka.http.scaladsl.server.RouteConcatenation._
import com.dss.vstore.utils.modules.ParsedConfig
import com.dss.vstore.utils.types.AuthorizationString.AuthorizationString
import com.dss.vstore.logic.models.{ClientData, ClientMeta}

import scala.concurrent.ExecutionContext

object ClientApiRoute {

  class ClientApiRoutes(client: Requester)(implicit modules: ModulesChain) {

    implicit val materializer: ActorMaterializer = modules.materializer
    implicit val system:       ActorSystem       = modules.system
    implicit val ec:           ExecutionContext  = modules.system.dispatcher
    implicit val config:       ParsedConfig      = modules.config


    /**
      * Returns a [[Route]] which handles bucket operations
      */
    def apply(client: ClientMeta, auth: AuthorizationString)(implicit modules: ModulesChain): Route = {
      lazy val loginHash = encrypter.getLoginHash(auth)
      lazy val passwordHash = encrypter.getPasswordHash(auth)

      def getClient = ClientGetRoute(client).getClient(loginHash)

      def headClient = ClientHeadRoute(client).headClient(loginHash)

      def putClient = ClientPutRoute(client).putClient(loginHash, passwordHash)

      def postClient = ClientPostRoute(client).postClient(loginHash)

      def deleteClient = ClientDeleteRoute(client).deleteClient(loginHash)

      encodeResponseWith(NoCoding, Gzip, Deflate) {
        getClient ~
          headClient ~
          putClient ~
          postClient ~
          deleteClient
      }
    }
  }

}
