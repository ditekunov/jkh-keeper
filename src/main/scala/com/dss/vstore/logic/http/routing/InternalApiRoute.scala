package com.dss.vstore.logic.http.routing

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.RouteDirectives.complete

object InternalApiRoute {

  def apply(): Route = { complete("INTERNAL API HERE") } //TODO

}