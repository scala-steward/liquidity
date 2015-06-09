package com.dhpcs.liquidity.models

import java.util.UUID

import com.dhpcs.json.ValueFormat

trait Identifier {

  def id: UUID

}

abstract class IdentifierCompanion[I <: Identifier] {

  implicit val IdentifierFormat = ValueFormat[I, UUID](apply, _.id)

  def apply(id: UUID): I

  def generate = apply(UUID.randomUUID)

}