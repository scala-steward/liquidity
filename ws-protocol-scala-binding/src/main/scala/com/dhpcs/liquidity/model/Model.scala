package com.dhpcs.liquidity.model

import okio.ByteString

final case class PublicKey(value: ByteString) {
  lazy val fingerprint: String = value.sha256.hex
}

object PublicKey {
  def apply(value: Array[Byte]): PublicKey = PublicKey(ByteString.of(value: _*))
}

final case class MemberId(value: String)

final case class Member(id: MemberId,
                        ownerPublicKeys: Set[PublicKey],
                        name: Option[String],
                        metadata: Option[com.google.protobuf.struct.Struct])

final case class AccountId(value: String)

final case class Account(id: AccountId,
                         ownerMemberIds: Set[MemberId],
                         name: Option[String],
                         metadata: Option[com.google.protobuf.struct.Struct])

final case class TransactionId(value: String)

final case class Transaction(
    id: TransactionId,
    from: AccountId,
    to: AccountId,
    value: BigDecimal,
    creator: MemberId,
    created: Long,
    description: Option[String],
    metadata: Option[com.google.protobuf.struct.Struct])

final case class ZoneId(value: String) {
  def persistenceId: String = s"${ZoneId.PersistenceIdPrefix}$value"
}

object ZoneId {

  final val PersistenceIdPrefix = "zone-"

  def fromPersistenceId(persistenceId: String): ZoneId =
    ZoneId(persistenceId.stripPrefix(PersistenceIdPrefix))

}

final case class Zone(id: ZoneId,
                      equityAccountId: AccountId,
                      members: Map[MemberId, Member],
                      accounts: Map[AccountId, Account],
                      transactions: Map[TransactionId, Transaction],
                      created: Long,
                      expires: Long,
                      name: Option[String],
                      metadata: Option[com.google.protobuf.struct.Struct])