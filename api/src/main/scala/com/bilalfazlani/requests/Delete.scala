package com.bilalfazlani.requests

import io.bullet.borer.Codec
import io.bullet.borer.derivation.MapBasedCodecs

case class Delete(id: Int)

object Delete {
  implicit val codec: Codec[Delete] = MapBasedCodecs.deriveCodec[Delete]
}
