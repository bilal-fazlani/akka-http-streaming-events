package com.bilalfazlani

import akka.actor.typed.scaladsl.AskPattern.Askable
import akka.actor.typed.{ActorRef, Scheduler}
import akka.util.Timeout
import com.bilalfazlani.EntityActor._

import scala.concurrent.Future

class EntityActiveObject(actorRef: ActorRef[EntityMessage])(implicit
    timeout: Timeout,
    scheduler: Scheduler
) {
  def getAll: Future[Set[Entity]] = actorRef ? GetAll

  def add(name: String): Future[Int] = actorRef ? (Add(name, _))

  def delete(id: Int): Future[Unit] = actorRef ? (Delete(id, _))
}
