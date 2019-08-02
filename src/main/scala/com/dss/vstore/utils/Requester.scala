package com.dss.vstore.utils

/**
  * Represents an abstract requester of the application
  */
trait Requester

/**
  * Represents superuser of the system
  */
case object Admin extends Requester

/**
  * Represents non-admin client
  */
case object Client extends Requester

/**
  * Represents bank client
  */
case object Bank extends Requester

/**
  * Represents receiver
  */
case object Receiver extends Requester

/**
  * Represents counter/meter
  */
case object Counter extends Requester

/**
  * Represents receiver client
  */
case object ReceiverClient extends Requester

/**
  * Represents receiver admin
  */
case object ReceiverAdmin extends Requester

/**
  * Represents undefined client
  */
case object NotClient extends Requester