package com.dss.vstore.utils.modules

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

/** Contains members used for running an Akka actor system */
trait ActorModule {
  implicit val system: ActorSystem
  implicit val materializer: ActorMaterializer
}

/** Implements members used for running an Akka actor system */
trait ActorModuleImpl extends ActorModule {
  this: ConfigurationModule =>
  override implicit val system: ActorSystem             = ActorSystem("vstore")
  override implicit val materializer: ActorMaterializer = ActorMaterializer()
}