package com.dss.vstore.utils.types

import eu.timepit.refined._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.boolean.AllOf
import eu.timepit.refined.collection.{MaxSize, MinSize}
import shapeless.{::, HNil}

import scala.util.Try

object AuthorizationString {

  type AuthorizationString = Refined[String, AuthorizationStringPredicate]

  object AuthorizationString {
    def unapply(arg: AuthorizationString): Option[String] = Some(arg.value)

    def apply(arg: String): Try[AuthorizationString] = refineV[AuthorizationStringPredicate](arg)
  }

  private type AuthorizationStringPredicate = AllOf[minIdSize :: maxIdSize :: HNil]

  /**
    * Types, that validate th length of auth string
    */
  private type maxIdSize = MaxSize[W.`64`.T]
  private type minIdSize = MinSize[W.`64`.T]
}