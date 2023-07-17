package main

import at.favre.lib.crypto.bcrypt.BCrypt

@main def bcryptString(): Unit =
  val hash = BCrypt.withDefaults.hashToString(10, "passFranco".toCharArray)
  val result = BCrypt.verifyer.verify("password".toCharArray, hash)
  println(hash)
