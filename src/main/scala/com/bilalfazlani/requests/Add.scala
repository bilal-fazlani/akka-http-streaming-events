package com.bilalfazlani.requests

import io.bullet.borer.Codec
import io.bullet.borer.derivation.MapBasedCodecs

case class Add(name: String)

object Add {
  implicit val codec: Codec[Add] = MapBasedCodecs.deriveCodec[Add]
}
