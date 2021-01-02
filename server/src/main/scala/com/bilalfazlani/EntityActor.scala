package com.bilalfazlani

import akka.NotUsed
import akka.actor.typed.SpawnProtocol.{Command, Spawn}
import akka.actor.typed._
import akka.actor.typed.scaladsl.AskPattern.Askable
import akka.actor.typed.scaladsl.Behaviors
import akka.stream.scaladsl.{BroadcastHub, Keep, Source}
import akka.stream.{Materializer, OverflowStrategy}
import akka.util.Timeout
import com.bilalfazlani.EntityActor.{EntityMessage, GetAll}
import com.bilalfazlani.responses.Event
import com.bilalfazlani.responses.Event.{Added, Deleted}

import scala.concurrent.Future
import scala.util.Random

class EntityActor() {
  def beh(state: Set[Entity]): Behavior[EntityMessage] = {
    Behaviors.setup { ctx =>
      lazy val (watcher, source) = Source
        .actorRef[Event](
          PartialFunction.empty,
          PartialFunction.empty,
          1,
          OverflowStrategy.fail
        )
        .toMat(BroadcastHub.sink)(Keep.both)
        .run()(Materializer(ctx.system))
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
  }
}
object EntityActor {
  sealed trait EntityMessage
  case class Add(name: String, replyTo: ActorRef[Int]) extends EntityMessage
  case class Delete(id: Int, replyTo: ActorRef[Unit]) extends EntityMessage
  case class GetAll(replyTo: ActorRef[Set[Entity]]) extends EntityMessage
  case class Subscribe(replyTo: ActorRef[Source[Event, NotUsed]])
      extends EntityMessage

  def start(implicit
      as: ActorSystem[Command],
      scheduler: Scheduler,
      timeout: Timeout
  ): Future[ActorRef[EntityMessage]] = {
    as ? (Spawn(
      new EntityActor().beh(Set.empty),
      "actor",
      Props.empty,
      _
    ))
  }
}
