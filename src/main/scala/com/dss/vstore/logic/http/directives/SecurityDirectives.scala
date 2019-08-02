package com.dss.vstore.logic.http.directives

import akka.http.scaladsl.server.{Directive0, Directive1}
import akka.http.scaladsl.server.directives.BasicDirectives.provide
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import com.dss.vstore.logic.{AccessDeniedError, RequesterDoesNotMatchError}
import com.dss.vstore.utils.{Bank, Client, Counter, Receiver, Requester}

object SecurityDirectives {

  def checkRequester(rawRequester: String): Directive1[Requester] = rawRequester match {
    case requester if requester == "client" => provide(Client)
    case requester if requester == "admin" => complete(AccessDeniedError("Admin is not able to access ClientRoute"))
    case requester if requester == "bank" => provide(Bank)
    case requester if requester == "counter" => provide(Counter)
    case requester if requester == "receiver" => provide(Receiver)
    case requester: String => complete(RequesterDoesNotMatchError(s"Unexpected requester entity: $requester"))
    case _ => complete(RequesterDoesNotMatchError("`Requester` header must be provided"))
  }

}
