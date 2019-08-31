package com.dss.vstore.utils.converters

import com.dss.vstore.utils.types.AuthorizationString.AuthorizationString
import com.dss.vstore.utils.types.Login.Login
import com.dss.vstore.utils.types.Password.Password
import com.dss.vstore.utils.types.Rights.Rights
import com.dss.vstore.utils.types.Signature.Signature

class TypeConversionsError(msg: String) extends Exception(msg)

object TypesHelper {
  def toAuthorizationString(input: String): AuthorizationString =
    AuthorizationString(input)
      .getOrElse(throw new TypeConversionsError(s"Could not converted string $input to authString"))

  def toSignature(input: String): Signature =
    Signature(input)
      .getOrElse(throw new TypeConversionsError(s"Could not converted $input to Signature"))

  def toLogin(input: String): Login =
    Login(input)
      .getOrElse(throw new TypeConversionsError(s"Could not converted string $input to LoginHash"))

  def toPassword(input: String): Password =
    Password(input)
      .getOrElse(throw new TypeConversionsError(s"Could not converted string $input to PasswordHash"))

  def toRight(input: String): Rights =
    Rights(input)
      .getOrElse(throw new TypeConversionsError(s"Could not converted string $input to Rights"))

}
