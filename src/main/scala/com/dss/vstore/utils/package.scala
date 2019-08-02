package com.dss.vstore

import com.dss.vstore.utils.modules.{ActorModule, ConfigurationModule, PersistenceModule}

package object utils {

  type ModulesChain = ConfigurationModule with ActorModule with PersistenceModule

}
