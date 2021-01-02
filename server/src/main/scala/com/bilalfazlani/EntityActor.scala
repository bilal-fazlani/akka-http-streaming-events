package com.bilalfazlani

import akka.actor.typed.SpawnProtocol.{Command, Spawn}
import akka.actor.typed._
import akka.actor.typed.scaladsl.AskPattern.Askable
import akka.actor.typed.scaladsl.Behaviors
import akka.util.Timeout
import com.bilalfazlani.EntityActor.{EntityMessage, GetAll}
import com.bilalfazlani.responses.Event
import com.bilalfazlani.responses.Event.{Added, Deleted}

import scala.concurrent.Future
import scala.util.Random

class EntityActor(watcher: ActorRef[Event]) {
  def beh(state: Set[Entity]): Behavior[EntityMessage] =
    Behaviors.receiveMessagePartial[EntityMessage] {
      case EntityActor.Add(name, replyTo) =>
        val entity = Entity(Random.nextInt().abs, name)
        replyTo ! entity.id
        watcher ! Added(entity)
        beh(state + entity)
      case EntityActor.Delete(id, replyTo) =>
        replyTo ! ()
        state.find(_.id == id).foreach(e => watcher ! Deleted(e))
        beh(state.filter(_.id != id))
      case GetAll(replyTo) =>
        replyTo ! state
        Behaviors.same
    }
}
object EntityActor {
  sealed trait EntityMessage
  case class Add(name: String, replyTo: ActorRef[Int]) extends EntityMessage
  case class Delete(id: Int, replyTo: ActorRef[Unit]) extends EntityMessage
  case class GetAll(replyTo: ActorRef[Set[Entity]]) extends EntityMessage

  def start(watcher: ActorRef[Event])(implicit
      as: ActorSystem[Command],
      scheduler: Scheduler,
      timeout: Timeout
  ): Future[ActorRef[EntityMessage]] = {
    as ? (Spawn(
      new EntityActor(watcher).beh(Set.empty),
      "actor",
      Props.empty,
      _
    ))
  }
}
