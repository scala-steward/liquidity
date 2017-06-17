package com.dhpcs.liquidity.server.serialization

import akka.actor.ExtendedActorSystem
import com.dhpcs.liquidity.actor.protocol._
import com.dhpcs.liquidity.model._
import com.dhpcs.liquidity.proto
import com.dhpcs.liquidity.serialization.ProtoBindingBackedSerializer
import com.dhpcs.liquidity.serialization.ProtoBindingBackedSerializer.AnyRefProtoBinding

import scala.collection.immutable.Seq

class ZoneValidatorMessageSerializer(system: ExtendedActorSystem)
    extends ProtoBindingBackedSerializer(
      system,
      protoBindings = Seq(
        AnyRefProtoBinding[EnvelopedZoneCommand, proto.actor.protocol.EnvelopedZoneCommand],
        AnyRefProtoBinding[ZoneCommandReceivedConfirmation, proto.actor.protocol.ZoneCommandReceivedConfirmation],
        AnyRefProtoBinding[ZoneAlreadyExists, proto.actor.protocol.ZoneAlreadyExists],
        AnyRefProtoBinding[ZoneRestarted, proto.actor.protocol.ZoneRestarted],
        AnyRefProtoBinding[EnvelopedZoneResponse, proto.actor.protocol.EnvelopedZoneResponse],
        AnyRefProtoBinding[ZoneNotificationWithIds, proto.actor.protocol.ZoneNotificationWithIds],
        AnyRefProtoBinding[ActiveZoneSummary, proto.actor.protocol.ActiveZoneSummary]
      ),
      identifier = 1668336332
    )
