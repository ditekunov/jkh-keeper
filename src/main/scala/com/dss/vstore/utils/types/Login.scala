package com.dss.vstore.utils.types

import eu.timepit.refined._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.boolean.AllOf
import eu.timepit.refined.collection.{MaxSize, MinSize}
import shapeless.{::, HNil}

import scala.util.Try


object Login {

  type Login = Refined[String, LoginPredicate]

  object Login {
    def unapply(arg: Login): Option[String] = Some(arg.value)

    def apply(arg: String): Try[Login] = refineV[LoginPredicate](arg)
  }

  private type LoginPredicate = AllOf[minLoginSize :: maxLoginSize :: HNil]

  /**
    * Types, that validate Login
    */
  private type maxLoginSize = MaxSize[W.`64`.T]
  private type minLoginSize = MinSize[W.`6`.T]
  //  private type loginRegex   = MatchesRegex[W.`^[a-z0-9_-]{3,16}$`.T] //TODO

}
