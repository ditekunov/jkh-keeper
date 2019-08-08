package com.dss.vstore.logic

object SpecificErrors {

  class ClientCannotBeCreatedError(reason: Throwable) extends Exception(reason)

  class ClientCannotBeObtainedError(reason: Throwable) extends Exception(reason)

  class WaterDataCannotBeTransferredError(reason: Throwable) extends Exception(reason)

  class GetClientError(reason: Throwable) extends Exception(reason)

  class GetCounterError(reason: Throwable) extends Exception(reason)

  class GetReceiverError(reason: Throwable) extends Exception(reason)

  class GetAdminError(reason: Throwable) extends Exception(reason)

  class UpdateClientError(reason: Throwable) extends Exception(reason)

  class WaterInputIsEmpty extends Exception

  class ElectricityInputIsEmpty extends Exception

  class ClientAlreadyEnabled extends Exception

  class ClientAlreadyDisabled extends Exception

  class ClientStatusInputIsEmpty extends Exception

}