package com.dss.vstore.utils.modules
import com.typesafe.config.{Config, ConfigFactory}
import javax.net.ssl.SSLContext

import scala.util.{Failure, Success, Try}

case class ParsedConfig(mainBlock: MainBlock, misc: Misc)

case class MainBlock(endpointConfig: EndpointConfig, timeouts: Timeouts)

case class EndpointConfig(host: String, port: Int)
case class Timeouts(bootingTimeout: Int)
case class Misc(healthcheckUrl: String)

trait ConfigurationModule {
  val config: ParsedConfig
  val encrypter: Encrypter
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
    ),
    Misc(
      tryConfig(configLoaded.getString("misc.healthcheck_url")).getOrElse("http://localhost:8080/vmanage/healthcheck")
    )
  )

  def tryConfig[T](f: T): Option[T] = Try(f) match {
    case Success(conf) => Some(conf)
    case Failure(_) => None
  }

  override lazy val encrypter: Encrypter = new Encrypter
}