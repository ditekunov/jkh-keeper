package com.dss.vstore.logic.http.routing

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.RouteDirectives.complete

object ServiceRoute {

  def apply(): Route = { complete("SERVICE API HERE") } //TODO

}