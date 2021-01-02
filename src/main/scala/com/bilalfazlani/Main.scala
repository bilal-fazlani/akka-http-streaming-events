package com.bilalfazlani

import akka.actor.typed.SpawnProtocol.Command
import akka.actor.typed.{ActorRef, ActorSystem, Scheduler, SpawnProtocol}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives
import akka.util.Timeout

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}

object Main extends App with Directives {
  private def wait[A](f: Future[A]) = Await.result(f, 5.seconds)

  implicit val actorSystem: ActorSystem[Command] =
    ActorSystem(SpawnProtocol(), "actor-system")
  implicit val scheduler: Scheduler = actorSystem.scheduler
  implicit val timeout: Timeout = Timeout(5.seconds)
  implicit val actorRef: ActorRef[EntityActor.EntityMessage] =
    wait(EntityActor.start)
  private val activeObject = new EntityActiveObject(actorRef)
  val routes = new EntityRoutes(activeObject)

  wait(
    Http()
      .newServerAt("0.0.0.0", 9090)
      .bind(routes.routes)
  )

  println("server started at http://localhost:9090")
}
