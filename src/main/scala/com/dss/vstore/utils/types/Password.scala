package com.dss.vstore.utils.types

import eu.timepit.refined.api.Refined
import eu.timepit.refined.boolean.AllOf
import eu.timepit.refined.collection.{MaxSize, MinSize}
import eu.timepit.refined.{W, refineV}
import shapeless.{::, HNil}

import scala.util.Try

object Password {

  type Password = Refined[String, PasswordPredicate]

  object Password {
    def unapply(arg: Password): Option[String] = Some(arg.value)

    def apply(arg: String): Try[Password] = refineV[PasswordPredicate](arg)
  }

  private type PasswordPredicate = AllOf[minPasswordSize :: maxPasswordSize :: HNil]

  /**
    * Types, that validate Password
    */
  private type maxPasswordSize = MaxSize[W.`128`.T]
  private type minPasswordSize = MinSize[W.`8`.T]
  //  private type Password
  // Regex   = MatchesRegex[W.`^[a-z0-9_-]{3,16}$`.T] //TODO

}
