package com.dss.vstore.logic.models

import java.time.LocalDateTime
import java.util.UUID

import com.dss.vstore.utils.{ModulesChain, Requester}

final case class ClientMeta(client_id: UUID,
                            client_login: LoginHash,
                            client_acl: PasswordHash,
                            created: LocalDateTime,
                            is_enabled: Boolean = true,
                            is_debtor:  Boolean = false,
                            is_archived: Option[Boolean] = None
                           )  extends Requester {
}

object ClientMeta {

  val createQuery =
    """CREATE TABLE IF NOT EXISTS clientmeta(
      client_id     uuid      PRIMARY KEY,
      client_login  text,
      client_acl    text,
      created       text,
      is_enabled    boolean,
      is_debtor     boolean,
      is_archived   boolean
      );"""

  val loginIndex = "CREATE INDEX IF NOT EXISTS client_login_index ON clientmeta(client_login);"

  def apply(client_id: UUID, client_login: LoginHash, clientAcl: PasswordHash): ClientMeta =
    new ClientMeta(client_id,
      client_login,
      clientAcl,
      LocalDateTime.now()
    )
}