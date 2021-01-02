package com.bilalfazlani

import akka.actor.typed.SpawnProtocol.Command
import akka.actor.typed.scaladsl.adapter.ClassicActorRefOps
import akka.actor.typed.{ActorRef, ActorSystem, Scheduler, SpawnProtocol}
import akka.http.scaladsl.Http
import akka.stream.OverflowStrategy
import akka.stream.scaladsl.{BroadcastHub, Keep, Source}
import akka.util.Timeout
import com.bilalfazlani.responses.Event

import scala.concurrent.duration.DurationInt

class Server {
  implicit lazy val actorSystem: ActorSystem[Command] =
    ActorSystem(SpawnProtocol(), "actor-system")
  implicit lazy val scheduler: Scheduler = actorSystem.scheduler
  implicit lazy val timeout: Timeout = Timeout(5.seconds)
  implicit lazy val (watcher, source) = Source
    .actorRef[Event](
      PartialFunction.empty,
      PartialFunction.empty,
      1,
      OverflowStrategy.fail
    )
    .toMat(BroadcastHub.sink)(Keep.both)
    .run()
  implicit lazy val actorRef: ActorRef[EntityActor.EntityMessage] =
    EntityActor.start(watcher.toTyped).block
  lazy val activeObject = new EntityActiveObject(actorRef)
  lazy val routes = new EntityRoutes(activeObject, source)

  def start(): Unit = {
    Http()
      .newServerAt("0.0.0.0", 9090)
      .bind(routes.routes)
      .block

    println("server started at http://localhost:9090")
  }
}
