package com.bilalfazlani

import akka.NotUsed
import akka.actor.typed.SpawnProtocol.{Command, Spawn}
import akka.actor.typed._
import akka.actor.typed.scaladsl.AskPattern.Askable
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.scaladsl.adapter.ClassicActorRefOps
import akka.stream.OverflowStrategy
import akka.stream.scaladsl.{BroadcastHub, Keep, Source}
import akka.util.Timeout
import com.bilalfazlani.EntityActor.{EntityMessage, GetAll, Reset, Subscribe}
import com.bilalfazlani.responses.Event
import com.bilalfazlani.responses.Event.{Added, Deleted}

import scala.concurrent.Future
import scala.util.Random

class EntityActor(watcher: ActorRef[Event], source: Source[Event, NotUsed]) {
  def beh(state: Set[Entity]): Behavior[EntityMessage] = {
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
      case Subscribe(replyTo) =>
        replyTo ! Source.single(Event.Init(state)).concat(source)
        Behaviors.same
      case Reset(replyTo) =>
        replyTo ! state.size
        watcher ! Event.Reset
        beh(Set.empty)
    }
  }
}
object EntityActor {
  sealed trait EntityMessage
  case class Add(name: String, replyTo: ActorRef[Int]) extends EntityMessage
  case class Delete(id: Int, replyTo: ActorRef[Unit]) extends EntityMessage
  case class GetAll(replyTo: ActorRef[Set[Entity]]) extends EntityMessage
  case class Reset(replyTo: ActorRef[Int]) extends EntityMessage
  case class Subscribe(replyTo: ActorRef[Source[Event, NotUsed]])
      extends EntityMessage

  def start(implicit
      as: ActorSystem[Command],
      scheduler: Scheduler,
      timeout: Timeout
  ): Future[ActorRef[EntityMessage]] = {
    val (watcher, source) = Source
      .actorRef[Event](
        PartialFunction.empty,
        PartialFunction.empty,
        10,
        OverflowStrategy.fail
      )
      .toMat(BroadcastHub.sink)(Keep.both)
      .run()
    as ? (Spawn(
      new EntityActor(watcher.toTyped, source).beh(Set.empty),
      "actor",
      Props.empty,
      _
    ))
  }
}
