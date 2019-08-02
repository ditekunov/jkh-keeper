package com.dss.vstore.utils.modules
import com.typesafe.config.{Config, ConfigFactory}

import scala.util.{Failure, Success, Try}

case class ParsedConfig(mainBlock: MainBlock)

case class MainBlock(endpointConfig: EndpointConfig, timeouts: Timeouts)

case class EndpointConfig(host: String, port: Int)
case class Timeouts(bootingTimeout: Int)

trait ConfigurationModule {
  val config: ParsedConfig

}

trait ConfigurationModuleImpl extends ConfigurationModule {

  lazy val configLoaded: Config = ConfigFactory.load("application.conf")

  val config = ParsedConfig(
    MainBlock(
      EndpointConfig(
        tryConfig(configLoaded.getString("main_block.endpoint.host")).getOrElse("127.0.0.1"),
        tryConfig(configLoaded.getInt("main_block.endpoint.host")).getOrElse(8080)
      ),
      Timeouts(
        tryConfig(configLoaded.getInt("main_block.timeouts.booting_timeout")).getOrElse(60)
      )
    )
  )

  def tryConfig[T](f: T): Option[T] = Try(f) match {
    case Success(conf) => Some(conf)
    case Failure(_) => None
  }
}