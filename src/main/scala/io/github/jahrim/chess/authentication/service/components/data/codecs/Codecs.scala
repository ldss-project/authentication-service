package io.github.jahrim.chess.authentication.service.components.data.codecs

import org.bson.conversions.Bson

/** [[Bson]] codecs of the authentication service. */
object Codecs:
  export UserCodec.given
  export TokenCodec.given
  export UserSessionCodec.given
  export JsonObjectCodec.given
