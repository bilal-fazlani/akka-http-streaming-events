package com.bilalfazlani

import akka.NotUsed
import akka.stream.scaladsl.Source
import com.bilalfazlani.responses.Event

import scala.concurrent.Future

class Client {
  def getAll: Future[Set[Entity]] = ???

  def add(name: String): Future[Int] = ???

  def delete(id: Int): Future[Unit] = ???

  def subscribe: Future[Source[Event, NotUsed]] = ???
}
