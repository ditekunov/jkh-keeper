package com.dss.vstore.utils

import com.dss.vstore.utils.types.AuthorizationString.AuthorizationString
import com.dss.vstore.utils.types.Login.Login
import com.dss.vstore.utils.types.Password.Password

package object modules {

  class Encrypter {

    def getLogin(auth: AuthorizationString): Login = {
      ???
    }

    def getPassword(auth: AuthorizationString): Password = {
      ???
    }

  }

}
