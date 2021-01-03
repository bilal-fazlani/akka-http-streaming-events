package com.bilalfazlani.responses

import com.bilalfazlani.Entity
import io.bullet.borer.derivation.MapBasedCodecs
import io.bullet.borer.{AdtEncodingStrategy, Codec}

sealed trait Event

object Event {
  case class Init(entities: Set[Entity]) extends Event
  case class Added(entity: Entity) extends Event
  case class Deleted(entity: Entity) extends Event
  case object Reset extends Event

  implicit val flatAdtEncoding: AdtEncodingStrategy =
    AdtEncodingStrategy.flat(typeMemberName = "_type")
  implicit val codec: Codec[Event] = MapBasedCodecs.deriveAllCodecs[Event]
}
