package com.bilalfazlani

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors

object Main extends App {
  implicit val actorSystem: ActorSystem[Nothing] =
    ActorSystem(Behaviors.empty, "actorSystem")
  import actorSystem.executionContext
  val client = new EntityClient("localhost", 9090)

  client.subscribe
    .flatMap(s => s.runForeach(println))
  Thread.sleep(2000)

  val id1 = client.add("a").block
  Thread.sleep(2000)

  val id2 = client.add("b").block
  Thread.sleep(2000)

  client.delete(id1)
  Thread.sleep(2000)

  client.delete()
}
