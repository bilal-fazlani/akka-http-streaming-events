package com.bilalfazlani

import akka.http.scaladsl.server.Directives

object Main extends App with Directives {
  val server = new Server()
  server.start()
}
