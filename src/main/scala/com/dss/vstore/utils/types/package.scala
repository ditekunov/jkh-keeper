package com.dss.vstore.utils

import scala.util.{Failure, Success, Try}

package object types {
  case class TypeValidationException(msg: String) extends Exception(msg)

  implicit def either2Try[T](income: Either[String, T]): Try[T] = {
    income match {
      case Right(message) => Success(message)
      case Left(ex) => Failure(TypeValidationException(ex))
    }
  }

}
