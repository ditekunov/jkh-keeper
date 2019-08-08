package com.dss.vstore.logic.http.routing

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.RouteDirectives.complete

object BankApiRoute {

  def apply(): Route = { complete("BANK API HERE") } //TODO

}