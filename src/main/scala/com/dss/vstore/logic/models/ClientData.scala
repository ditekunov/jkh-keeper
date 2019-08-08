package com.dss.vstore.logic.models

import java.time.LocalDateTime
import java.util.UUID

final case class ClientData(client_id:                 UUID,
                            curr_electricity_1:        Option[Double]           = None,
                            curr_electricity_2:        Option[Double]           = None,
                            curr_electricity_3:        Option[Double]           = None,
                            curr_electricity_legacy:   Option[Double]           = None,
                            curr_electricity_abstract: Option[Double]           = None,
                            last_electricity: 	       Option[LocalDateTime]    = None,
                            curr_wat_heat:             Option[Double]           = None,
                            curr_wat_cold:             Option[Double]           = None,
                            curr_wat_abstract:         Option[Double]           = None,
                            last_wat: 		             Option[LocalDateTime]    = None,
                            debt: 		                 Option[Double]           = None)

object ClientData {

  val createQuery =
    """CREATE TABLE IS NOT EXISTS client_data(
      client_id                 uuid        PRIMARY KEY, // Id of a user
      curr_electricity_1        double,   	             // Current balance of a user in electricity phase 1
      curr_electricity_2        double,	                 // Current balance of a user in electricity phase 2
      curr_electricity_3        double,                  // Current balance of a user in electricity phase 3
      curr_electricity_legacy   double, 	               // Current balance of a user without phase delimeter
      curr_electricity_abstract double,
      last_electricity          timestamp, 	             // Last date when user transmitted data about electricity
      curr_wat_heat             double, 	               // Current balance of a user in heat water
      curr_wat_cold             double, 	               // Current balance of a user in cold water
      curr_wat_abstract         double,
      last_wat                  timestamp, 	             // Last date when user transmitted data about water
      debt                      double 		               // Sum of all current debts
      ); """

  def apply(client_id: UUID): ClientData = new ClientData(client_id)
}
