package com.bilalfazlani

import akka.actor.typed.SpawnProtocol.{Command, Spawn}
import akka.actor.typed.scaladsl.AskPattern.Askable
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed._
import akka.util.Timeout
import com.bilalfazlani.EntityActor.{EntityMessage, GetAll}

import scala.concurrent.Future
import scala.util.Random

class EntityActor {
//  case class ActorState()

  def beh(state: Set[Entity]): Behavior[EntityMessage] =
    Behaviors.receiveMessagePartial[EntityMessage] {
      case EntityActor.Add(name, replyTo) =>
        val entity = Entity(Random.nextInt().abs, name)
        replyTo ! entity.id
        beh(state + entity)
      case EntityActor.Delete(id, replyTo) =>
        replyTo ! ()
        beh(state.filter(_.id != id))
      case GetAll(replyTo) =>
        replyTo ! state
        Behaviors.same
//    case EntityActor.Subscribe() =>
    }
}
object EntityActor {
  sealed trait EntityMessage
  case class Add(name: String, replyTo: ActorRef[Int]) extends EntityMessage
  case class Delete(id: Int, replyTo: ActorRef[Unit]) extends EntityMessage
  case class GetAll(replyTo: ActorRef[Set[Entity]]) extends EntityMessage
//  case class Subscribe() extends EntityMessage

  def start(implicit
      as: ActorSystem[Command],
      scheduler: Scheduler,
      timeout: Timeout
  ): Future[ActorRef[EntityMessage]] = {
    as ? (Spawn(new EntityActor().beh(Set.empty), "actor", Props.empty, _))
  }
}
