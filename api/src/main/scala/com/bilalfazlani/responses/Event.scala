package com.bilalfazlani.responses

import com.bilalfazlani.Entity
import io.bullet.borer.derivation.MapBasedCodecs
import io.bullet.borer.{AdtEncodingStrategy, Codec}

sealed trait Event

object Event {
  case class Init(data: Set[Entity]) extends Event
  case class Added(entity: Entity, data: Set[Entity]) extends Event
  case class Deleted(entity: Entity, data: Set[Entity]) extends Event
  case class Reset(data: Set[Entity]) extends Event

  implicit val flatAdtEncoding: AdtEncodingStrategy =
    AdtEncodingStrategy.flat(typeMemberName = "_type")
  implicit val codec: Codec[Event] = MapBasedCodecs.deriveAllCodecs[Event]
}
