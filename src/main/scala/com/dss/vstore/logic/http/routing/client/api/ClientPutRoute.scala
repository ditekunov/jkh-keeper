package com.dss.vstore.logic.http.routing.client.api

import java.util.UUID
import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpEntity, HttpRequest}
import akka.http.scaladsl.server.Directives.extractStrictEntity
import akka.http.scaladsl.server.{Route, StandardRoute}
import akka.stream.ActorMaterializer
import com.dss.vstore.logic.models.{ClientData, ClientMeta}
import com.dss.vstore.utils.ModulesChain
import akka.http.scaladsl.server.directives.MethodDirectives.put
import akka.http.scaladsl.server.directives.FutureDirectives.onComplete
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.http.scaladsl.server.RouteConcatenation._
import com.dss.vstore.logic.{ClientAlreadyExistsError, InvalidRequestError}
import com.dss.vstore.utils.modules.ParsedConfig
import com.dss.vstore.utils.types.LoginHash.LoginHash
import com.dss.vstore.utils.types.PasswordHash.PasswordHash
import com.dss.vstore.logic.{ClientCannotBeCreatedError}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.FiniteDuration
import scala.util.{Failure, Success, Try}

class ClientPutRoute(client: ClientMeta)(implicit modules: ModulesChain) {

  implicit val materializer: ActorMaterializer = modules.materializer
  implicit val system:       ActorSystem       = modules.system
  implicit val ec:           ExecutionContext  = modules.system.dispatcher
  implicit val config:       ParsedConfig      = modules.config

  final private val entityParsingTimeout       = FiniteDuration(10, TimeUnit.SECONDS)

  /**
    * Returns a [[Route]] that handles PUT operations on Clients
    */
  def putClient(loginHash: LoginHash, passwordHash: PasswordHash)(implicit modules: ModulesChain, ec: ExecutionContext): Route = {
//    Logger.debug(s"PUT_CLIENT_BENCH $getCurrentLocalDateTimeStamp start putClient")
    put {
      createClient(loginHash: LoginHash, passwordHash: PasswordHash) ~
        putManualWater(loginHash: LoginHash) ~
        putManualElectricity(loginHash: LoginHash) ~
        putClientStatus(loginHash: LoginHash)
    }
  }

  /**
    * Adds new [[ClientMeta]] and [[ClientData]] to a system
    */
  def createClient(loginHash: LoginHash, password: PasswordHash): Route = {
    val newId = UUID.randomUUID()

    onComplete(modules.clientDal.getClientByLoginHash(loginHash)) {
      case Success(clients) if clients.isEmpty =>
        onComplete(modules.clientDal.fullClientCreation(
          ClientMeta(newId, loginHash, password))) {
          case Success(_) => complete("Client successfully created")
          case Failure(ex) => complete(new ClientCannotBeCreatedError(ex))
        }
      case Success(_) => complete(ClientAlreadyExistsError("Client with given login already exists"))
      case Failure(ex) => complete(new ClientCannotBeCreatedError(ex))
    }

  }

  /**
    * Handles PUT requests with water
    */
  def putManualWater(loginHash: LoginHash): Route =
    extractStrictEntity(entityParsingTimeout) { entity: HttpEntity.Strict =>

      /**
        * Checks, whether values given in PUT water request are valid
        */
      def isValidWater(heatWater: BigInt, coldWater: BigInt, abstractWater: BigInt, datetime: String) =
        ( (heatWater > 0 && coldWater > 0) || (abstractWater > 0) ) && Try(ZonedDateTime(datetime)).isSuccess

      /**
        * Checks, whether values given in PUT water request are equal to zero
        */
      def waterZeroInput(heatWater: BigInt, coldWater: BigInt, abstractWater:BigInt, datetime: String) =
        (heatWater == 0 || coldWater == 0) && (abstractWater == 0) && Try(ZonedDateTime(datetime)).isSuccess

      parseWater(entity.getData().utf8String).headOption match {

        case Some((heatWater, coldWater, abstractWater, datetime))
          if isValidWater(heatWater, coldWater, abstractWater, datetime)  =>
          onComplete(modules.clientDal.getClientByLoginHash(loginHash)) {
            case Success(clients) if clients.nonEmpty =>
              onComplete(modules.clientDataDal.getClientData(clients.head.client_id)) {
                case Success(data) if data.nonEmpty => complete("") //TODO:!!!!
                case Failure(ex) => complete("") //TODO:!!!!
              }
            case Failure(ex) => complete(new GetClientError(ex))
          }

        case Some((heatWater, coldWater, abstractWater, datetime))
          if waterZeroInput(heatWater, coldWater, abstractWater, datetime) =>
          complete(new WaterInputIsEmpty)

        case Some(smth) => complete(InvalidRequestError(s"${smth.toString} are not valid values"))
        case None => complete(new WaterInputIsEmpty)
        case unknown => complete(InvalidRequestError(s"$unknown are not valid values"))
      }
    }

  /**
    * Handles PUT requests with electricity
    */
  def putManualElectricity(loginHash: LoginHash): Route =
    extractStrictEntity(entityParsingTimeout) { entity: HttpEntity.Strict =>

      /**
        * Checks, whether values given in PUT water request are valid
        */
      def isValidElectricity(phase1: BigInt,
                             phase2: BigInt,
                             phase3: BigInt,
                             legacy: BigInt,
                             abstractPhase: BigInt,
                             datetime: String) =
        ( (phase1 > 0 && phase2 > 0 && phase3 > 0) || (legacy > 0) || (abstractPhase > 0) ) &&
          Try(ZonedDateTime(datetime)).isSuccess

      /**
        * Checks, whether values given in PUT water request are equal to zero
        */
      def electricityZeroInput(phase1: BigInt,
                               phase2: BigInt,
                               phase3: BigInt,
                               legacy: BigInt,
                               abstractPhase: BigInt,
                               datetime: String) =
        ( (phase1 == 0 || phase2 == 0 || phase3 == 0) && (legacy == 0) && (abstractPhase == 0) ) &&
          Try(ZonedDateTime(datetime)).isSuccess

      parseElectricity(entity.getData().utf8String).headOption match {

        case Some((phase1, phase2, phase3, legacy, abstractPhase, datetime))
          if isValidElectricity(phase1, phase2, phase3, legacy, abstractPhase, datetime) =>
          onComplete(modules.clientDal.getClientByLoginHash(loginHash)) {
            case Success(clients) if clients.nonEmpty => complete("") //TODO: FINALIZE
            case Failure(ex) => complete(new GetClientError(ex))
          }

        case Some((phase1, phase2, phase3, legacy, abstractPhase, datetime))
          if electricityZeroInput(phase1, phase2, phase3, legacy, abstractPhase, datetime) =>
          complete(new ElectricityInputIsEmpty)

        case Some(smth) => complete(InvalidRequestError(s"${smth.toString} are not valid values"))
        case None => complete(new ElectricityInputIsEmpty)
        case unknown => complete(InvalidRequestError(s"$unknown are not valid values"))
      }
    }

  /**
    * Handles changing the status of a client
    */
  def putClientStatus(loginHash: LoginHash): Route =
    extractStrictEntity(entityParsingTimeout) { entity: HttpEntity.Strict =>
      parseClientStatus(entity.getData().utf8String).headOption match {

        case Some(statusToSet) => onComplete(modules.clientDal.getClientByLoginHash(loginHash)) {
          case Success(clients) if clients.nonEmpty => statusToSet match {
            case true if clients.head.is_enabled => complete(new ClientAlreadyEnabled)
            case false if !clients.head.is_enabled => complete(new ClientAlreadyDisabled)

            case validStatus => onComplete(modules.clientDal.updateClientStatus(clients.head, validStatus)) {
              case Success(_) => complete("Status successfully updated")
              case Failure(ex) => complete(new UpdateClientError(ex))
            }
          }
          case Failure(ex) => complete(new GetClientError(ex))
        }
        case None => complete(new ClientStatusInputIsEmpty)
      }
    }
}

object ClientPutRoute {

  def apply(client: ClientMeta)(implicit modules: ModulesChain): ClientPutRoute =
    new ClientPutRoute(client)
}