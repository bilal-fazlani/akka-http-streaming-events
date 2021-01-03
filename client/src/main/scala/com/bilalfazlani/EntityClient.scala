package com.bilalfazlani

import akka.NotUsed
import akka.actor.ClassicActorSystemProvider
import akka.stream.scaladsl.Source
import com.bilalfazlani.requests.Add
import com.bilalfazlani.responses.Event

import scala.concurrent.{ExecutionContext, Future}

class EntityClient(host: String, port: Int)(implicit
    classicActorSystemProvider: ClassicActorSystemProvider,
    ec: ExecutionContext
) extends HttpClient(host, port) {
  def getAll: Future[Set[Entity]] = get[Set[Entity]]("/entities")
  def add(name: String): Future[Int] = post[Add, Int]("/entities", Add(name))
  def delete(id: Int): Future[Unit] =
    delete[Option[Unit], Unit](s"/entities/$id")
  def subscribe: Future[Source[Event, NotUsed]] =
    sse[Event]("/entities/subscribe")
}
