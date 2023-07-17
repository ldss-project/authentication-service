package main

import io.github.jahrim.hexarc.persistence.bson.dsl.BsonDSL.{*, given}

import java.time.ZonedDateTime
@main def bsonError(): Unit =
  println(
    bson {
      "date" :: ZonedDateTime.now
    }
  )
