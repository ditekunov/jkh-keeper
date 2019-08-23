package com.dss.vstore.logic

sealed abstract class CustomError(mes: String) extends Throwable(mes) {
  val code: String
  val message: String
  val requestId: String

  val `AccessDenied`: String = "AccessDenied"
  val `EntityDoesNotMatch`: String = "EntityDoesNotMatch"
  val `InvalidAuthorizationString` = "InvalidAuthorizationString"
  val `InvalidRequest` = "InvalidRequest"
  val `InternalServerError` = "InternalServerError"
  val `SignatureDoesNotMatch` = "SignatureDoesNotMatch"
  val `NoSuchClient` = "NoSuchClient"
  val `ClientAlreadyExists` = "ClientAlreadyExists"
  val `NoSuchAdmin` = "NoSuchAdmin"
  val `InvalidPassword` = "InvalidPassword"
  val `NoSuchCounter` = "NoSuchCounter"
  val `NoSuchReceiver` = "NoSuchReceiver"
}


final case class AccessDeniedError(message: String, requestId: String = "")
  extends CustomError(message) {
  val code: String = `AccessDenied`
}

final case class RequesterDoesNotMatchError(message: String, requestId: String = "")
  extends CustomError(message) {
  val code: String = `EntityDoesNotMatch`
}

final case class SignatureDoesNotMatchError(message: String, requestId: String = "")
  extends CustomError(message) {
  val code: String = `SignatureDoesNotMatch`
}

final case class InvalidAuthorizationStringError(message: String, requestId: String = "")
  extends CustomError(message) {
  val code: String = `InvalidAuthorizationString`
}

final case class InvalidRequestError(message: String, requestId: String = "")
  extends CustomError(message) {
  val code: String = `InvalidRequest`
}

final case class InternalServerError(message: String, requestId: String = "")
  extends CustomError(message) {
  val code: String = `InternalServerError`
}

final case class NoSuchClientError(message: String, requestId: String = "")
  extends CustomError(message) {
  val code: String = `NoSuchClient`
}

final case class NoSuchAdminError(message: String = "", requestId: String = "")
  extends CustomError(message) {
  val code: String = `NoSuchAdmin`
}

final case class ClientAlreadyExistsError(message: String, requestId: String = "")
  extends CustomError(message) {
  val code: String = `ClientAlreadyExists`
}

final case class InvalidPasswordError(message: String = "", requestId: String = "")
  extends CustomError(message) {
  val code: String = `InvalidPassword`
}

final case class NoSuchCounterError(message: String = "", requestId: String = "")
  extends CustomError(message) {
  val code: String = `NoSuchCounter`
}

final case class NoSuchReceiverError(message: String = "", requestId: String = "")
  extends CustomError(message) {
  val code: String = `NoSuchReceiver`
}