package com.dss.vstore.utils.types

import eu.timepit.refined.api.Refined
import eu.timepit.refined.boolean.AllOf
import eu.timepit.refined.collection.{MaxSize, MinSize}
import eu.timepit.refined.{W, refineV}
import shapeless.{::, HNil}

import scala.util.Try

object Signature {
  type Signature = Refined[String, SignaturePredicate]

  object Signature {
    def unapply(arg: Signature): Option[String] = Some(arg.value)

    def apply(arg: String): Try[Signature] = refineV[SignaturePredicate](arg)
  }

  private type SignaturePredicate = AllOf[minIdSize :: maxIdSize :: HNil]

  /**
    * Types, that validate Client ID
    */
  private type maxIdSize = MaxSize[W.`64`.T]
  private type minIdSize = MinSize[W.`64`.T]


}