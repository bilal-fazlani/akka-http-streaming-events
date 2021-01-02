package com.bilalfazlani

import io.bullet.borer.Codec
import io.bullet.borer.derivation.MapBasedCodecs

case class Entity(id: Int, name: String)

object Entity {
  implicit val codec: Codec[Entity] = MapBasedCodecs.deriveCodec[Entity]
}
