package com.bilalfazlani

import akka.actor.typed.SpawnProtocol.Command
import akka.actor.typed.{ActorRef, ActorSystem, Scheduler, SpawnProtocol}
import akka.http.scaladsl.Http
import akka.util.Timeout

import scala.concurrent.duration.DurationInt

class Server {
  implicit lazy val actorSystem: ActorSystem[Command] =
    ActorSystem(SpawnProtocol(), "actor-system")
  implicit lazy val scheduler: Scheduler = actorSystem.scheduler
  implicit lazy val timeout: Timeout = Timeout(5.seconds)
  implicit lazy val actorRef: ActorRef[EntityActor.EntityMessage] =
    EntityActor.start.block
  lazy val activeObject = new EntityActiveObject(actorRef)
  lazy val routes = new EntityRoutes(activeObject)

  def start(): Unit = {
    Http()
      .newServerAt("0.0.0.0", 9090)
      .bind(routes.routes)
      .block

    println("server started at http://localhost:9090")
  }
}
