package com.dss.vstore.logic.http

import akka.actor.ActorSystem
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.dss.vstore.utils.ModulesChain
import akka.http.scaladsl.server.directives.HeaderDirectives.headerValueByName
import akka.http.scaladsl.server.Directives._
import com.dss.vstore.utils.StatsSupport.StatsHolder
import javax.net.ssl.SSLContext
import akka.http.scaladsl.{ConnectionContext, Http}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class EntryPoint(val modules: ModulesChain) {
  implicit val ec: ExecutionContext            = modules.system.dispatcher // Execution Context Executor
  implicit val system: ActorSystem             = modules.system            // Handles ActorSystem behavior
  implicit val materializer: ActorMaterializer = ActorMaterializer()       // Handles Actors behavior

  private var binding: Option[Future[ServerBinding]] = None

  val routes: Route =
    pathPrefix("vstore") {
      (headerValueByName("Client-Entity") & headerValueByName("Signature") & headerValueByName("Authorization")) {
        (rawRequester, maybeSignature, auth) => complete("200 OK")
      }
    }

  def main(sslContext: Option[SSLContext]): Try[StatsHolder] =
    Try({
      modules.system.log.info(s"Tables created")
      val statsHolder = new StatsHolder

      val endpointConf = modules.config.mainBlock.endpointConfig

      sslContext match {
        case Some(ssl) =>
          this.binding = Some(
            Http().bindAndHandle(routes,
              endpointConf.host,
              endpointConf.port,
              connectionContext = ConnectionContext.https(ssl))
          )
        case None => this.binding = Some(Http().bindAndHandle(routes, endpointConf.host, endpointConf.port))
      }

      println(s"Server online at http://${endpointConf.host}:${endpointConf.port}\nPress RETURN to stop...")
      statsHolder
    })

  def stop(): Unit = {
    unbind(this.binding)
  }

  def unbind(binding: Option[Future[ServerBinding]]): Unit = {
    binding match {
      case Some(bindingUnwrapped) =>
        bindingUnwrapped
          .flatMap(_.unbind())
          .onComplete({ _ =>
            modules.system
              .terminate()
              .map({ _ =>
                modules.dbConnectionClose
              })
          })
      case None =>
        modules.system
          .terminate()
          .map({ _ =>
            modules.dbConnectionClose
          })
    }
  }
}
