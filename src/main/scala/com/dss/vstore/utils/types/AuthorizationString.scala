package com.dss.vstore.utils.types

import eu.timepit.refined._
import eu.timepit.refined.api.{Refined, Validate}
import eu.timepit.refined.boolean.AllOf
import eu.timepit.refined.collection.{MaxSize, MinSize}
import eu.timepit.refined.string.MatchesRegex
import shapeless.{::, HNil}

import scala.util.{Failure, Success, Try}

object AuthorizationString {

  import types._

  type AuthorizationString = Refined[String, AuthorizationStringPredicate]
  object AuthorizationString {
    def unapply(arg: AuthorizationString): Option[String] = Some(arg.value)

    def apply(arg: String): Try[AuthorizationString] = refineV[AuthorizationStringPredicate](arg)
  }

  private type AuthorizationStringPredicate = AllOf[minIdSize :: maxIdSize :: HNil]

  /**
    * Types, that validate Client ID
    */
  private type maxIdSize = MaxSize[W.`64`.T]
  private type minIdSize = MinSize[W.`64`.T]


}