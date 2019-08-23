package com.dss.vstore.utils.types

import eu.timepit.refined._
import eu.timepit.refined.api.Validate.Plain
import eu.timepit.refined.api.{Refined, Validate}

import scala.util.Try

object Rights {
  type Rights = Refined[String, RightsPredicate]

  object Rights {
    def unapply(arg: Rights): Option[String] = Some(arg.value)

    def apply(arg: String): Try[Rights] = refineV[RightsPredicate](arg)
  }

  case class RightsPredicate()

  implicit val validateRights: Plain[String, RightsPredicate] =
    Validate.fromPredicate(
      (right: String) => ListOfRights.contains(right),
      right => s"Right $right does not exist",
      RightsPredicate()
    )

  final private val ListOfRights = Set(
    "read",
    "write",
    "truncate",
    "read-write",
    "read-truncate",
    "write-truncate",
    "superuser"
  )
}