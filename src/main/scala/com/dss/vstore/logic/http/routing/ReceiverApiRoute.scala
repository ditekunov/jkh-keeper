package com.dss.vstore.logic.http.routing

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.RouteDirectives.complete

object ReceiverApiRoute {

  def apply(): Route = { complete("RECEIVER API HERE") } //TODO

}