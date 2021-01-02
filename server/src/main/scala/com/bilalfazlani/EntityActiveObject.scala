package com.bilalfazlani

import akka.NotUsed
import akka.actor.typed.scaladsl.AskPattern.Askable
import akka.actor.typed.{ActorRef, Scheduler}
import akka.stream.scaladsl.Source
import akka.util.Timeout
import com.bilalfazlani.EntityActor._
import com.bilalfazlani.responses.Event

import scala.concurrent.Future

class EntityActiveObject(actorRef: ActorRef[EntityMessage])(implicit
    timeout: Timeout,
    scheduler: Scheduler
) {
  def getAll: Future[Set[Entity]] = actorRef ? GetAll

  def add(name: String): Future[Int] = actorRef ? (Add(name, _))

  def delete(id: Int): Future[Unit] = actorRef ? (Delete(id, _))

  def subscribe(): Future[Source[Event, NotUsed]] = actorRef ? Subscribe
}
