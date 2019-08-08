package com.dss.vstore.logic.http.routing

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.RouteDirectives.complete

object HealthcheckRoute {

  def apply(): Route = { complete("HEALTHCHECK API HERE") } //TODO

}