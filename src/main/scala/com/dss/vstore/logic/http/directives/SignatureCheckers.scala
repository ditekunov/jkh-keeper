package com.dss.vstore.logic.http.directives

import akka.http.scaladsl.server.{Directive0, Route}
import akka.http.scaladsl.server.directives.BasicDirectives.pass
import com.dss.vstore.logic.http.routing.BankApiRoute
import com.dss.vstore.utils._
import com.dss.vstore.utils.converters.TypesHelper.{toLoginHash, toPasswordHash, toSignature}
import com.dss.vstore.utils.types.Signature.Signature
import akka.http.scaladsl.server.directives.FutureDirectives.onComplete
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import com.dss.vstore.logic.models.ClientMeta
import com.dss.vstore.logic.{InternalServerError, NoSuchAdminError, NoSuchClientError, SignatureDoesNotMatchError}

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success, Try}

object SignatureCheckers {

  /**
    * Checks, whether the [[Signature]] of a [[Client]] is valid
    */
  def checkClientSignature(signature: Signature)
                          (implicit ec: ExecutionContext,  modules: ModulesChain): Directive0 = {
    Try(modules.encrypter.getLoginHashFromSignature(signature)) match {
      case Success(loginHash) =>
        onComplete(modules.clientDal.getClientByLoginHash(loginHash)) flatMap {
          case Success(clients) => clients.nonEmpty match {
            case true =>

              val trueSignature = encrypter.generateSignatureFromLogin(Client, clients.head.client_login)

              if (trueSignature.value == signature.value) pass
              else complete(SignatureDoesNotMatchError("Signature is wrong"))

            case false => complete(NoSuchClientError("Client with such login is not found"))
          }
          case Failure(_) => complete(InternalServerError("Cannot get client by signature"))
        }

      case Failure(_) => complete(SignatureDoesNotMatchError("Login is invalid"))
    }
  }

  /**
    * Checks, whether the [[Signature]] of a [[Admin]] is valid
    */
  def checkAdminSignature(signature: Signature)
                         (implicit ec: ExecutionContext,  modules: ModulesChain, encrypter: Encrypter): Directive0 = {
    Try(encrypter.getLoginHashFromSignature(signature)) match {
      case Success(loginHash) =>
        onComplete(modules.adminDal.getAdminByLoginHash(loginHash)) flatMap {
          case Success(admins) => admins.nonEmpty match {
            case true =>

              val trueSignature = encrypter.generateSignatureFromLogin(Admin, admins.head.admin_login)

              if (trueSignature.value == signature.value) pass
              else complete(SignatureDoesNotMatchError("Signature is wrong"))

            case false => complete(NoSuchAdminError("Admin with such login is not found"))
          }
          case Failure(_) => complete(InternalServerError("Cannot get admin by signature"))
        }

      case Failure(_) => complete(SignatureDoesNotMatchError("Login is invalid"))
    }
  }

  /**
    * Checks, whether the [[Signature]] of a [[Bank]] is valid
    */
  def checkBankSignature(signature: Signature)
                        (implicit ec: ExecutionContext,  modules: ModulesChain, encrypter: Encrypter): Directive0 = {
    Try(encrypter.getIdHashFromSignature(signature)) match {
      case Success(idHash) =>
        onComplete(modules.bankDal.getBankByIdHash(idHash)) flatMap {
          case Success(bankClients) => bankClients.nonEmpty match {
            case true =>

              val trueSignature = encrypter.generateSignatureFromId(Bank, bankClients.head.bank_id)

              if (trueSignature.value == signature.value) pass
              else complete(SignatureDoesNotMatchError("Signature is wrong"))

            case false => complete(NoSuchAdminError("Bank client with such id is not found"))
          }
          case Failure(_) => complete(InternalServerError("Cannot get bank client by signature"))
        }

      case Failure(_) => complete(SignatureDoesNotMatchError("Id is invalid"))
    }
  }

  /**
    * Checks, whether the [[Signature]] of a [[Counter]] is valid
    */
  def checkCounterSignature(signature: Signature)
                           (implicit ec: ExecutionContext,  modules: ModulesChain, encrypter: Encrypter): Directive0 = {
    Try(encrypter.getIdHashFromSignature(signature)) match {
      case Success(idHash) =>
        onComplete(modules.counterDal.getCounterByIdHash(idHash)) flatMap {
          case Success(counters) => counters.nonEmpty match {
            case true =>

              val trueSignature = encrypter.generateSignatureFromId(Counter, counters.head.counter_id)

              if (trueSignature.value == signature.value) pass
              else complete(SignatureDoesNotMatchError("Signature is wrong"))

            case false => complete(NoSuchAdminError("Counter with such id is not found"))
          }
          case Failure(_) => complete(InternalServerError("Cannot get counter by signature"))
        }

      case Failure(_) => complete(SignatureDoesNotMatchError("Id is invalid"))
    }
  }

  /**
    * Checks, whether the [[Signature]] of a [[Receiver]] is valid
    */
  def checkReceiverSignature(signature: Signature)
                            (implicit ec: ExecutionContext,  modules: ModulesChain, encrypter: Encrypter): Directive0 = {
    Try(encrypter.getIdHashFromSignature(signature)) match {
      case Success(idHash) =>
        onComplete(modules.receiverDal.getReceiverByIdHash(idHash)) flatMap {
          case Success(receivers) => receivers.nonEmpty match {
            case true =>

              val trueSignature = encrypter.generateSignatureFromId(Receiver, receivers.head.receiver_id)

              if (trueSignature.value == signature.value) pass
              else complete(SignatureDoesNotMatchError("Signature is wrong"))

            case false => complete(NoSuchAdminError("Counter with such id is not found"))
          }
          case Failure(_) => complete(InternalServerError("Cannot get counter by signature"))
        }

      case Failure(_) => complete(SignatureDoesNotMatchError("Id is invalid"))
    }
  }

}