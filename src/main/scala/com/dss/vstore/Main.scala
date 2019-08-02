package com.dss.vstore

import java.io.{PrintWriter, StringWriter}

import com.dss.vstore.logic.http.EntryPoint
import com.dss.vstore.utils.modules.{ActorModuleImpl, ConfigurationModuleImpl, PersistenceModuleImpl}

import scala.util.{Failure, Success, Try}

object Main extends App {

  trait Modules

  println("booting...")

  Try(new Modules with ConfigurationModuleImpl with ActorModuleImpl with PersistenceModuleImpl) match {
    case Failure(e) =>
      val sw = new StringWriter
      e.printStackTrace(new PrintWriter(sw))
      e match {
        case _ => println(s"Exception - ${sw.toString}")
      }
      System.exit(1)

    case Success(modules) =>
      import java.lang.management.ManagementFactory

      val rt = ManagementFactory.getRuntimeMXBean
      rt.getName


      val entryPoint = new EntryPoint(modules)

      entryPoint.main() match {
        case Success(stats) => modules.system.log.info(s"$stats")

        case Failure(e) =>
          entryPoint.stop()
      }

    case Failure(ex) =>
      println()
      println(s"Bad endpoint ssl config: ${ex.getMessage}")
  }
}


}


}
