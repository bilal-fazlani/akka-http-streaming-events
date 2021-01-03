package com.bilalfazlani

import akka.NotUsed
import akka.actor.{ActorSystem, ClassicActorSystemProvider}
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding.{Delete, Get, Post}
import akka.http.scaladsl.model.sse.ServerSentEvent
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.scaladsl.Source
import io.bullet.borer.compat.akkaHttp._
import io.bullet.borer.{Decoder, Encoder, Json}
import akka.http.scaladsl.unmarshalling.sse.EventStreamUnmarshalling._

import scala.concurrent.{ExecutionContext, Future}

class HttpClient(host: String, port: Int)(implicit
    classicActorSystemProvider: ClassicActorSystemProvider,
    ec: ExecutionContext
) {
  private val baseUrl = s"http://$host:$port"
  private def url(path: String) = s"$baseUrl$path"

  def get[A: Decoder](path: String): Future[A] =
    Http()
      .singleRequest(Get(url(path)))
      .flatMap(Unmarshal(_).to[A])

  def post[A: Encoder, B: Decoder](
      path: String,
      entity: A
  ): Future[B] =
    Http()
      .singleRequest(Post(url(path), entity))
      .flatMap(Unmarshal(_).to[B])

  def delete[A: Encoder, B: Decoder](
      path: String,
      entity: Option[A] = None
  ): Future[B] =
    Http()
      .singleRequest(Delete(url(path), entity))
      .flatMap(Unmarshal(_).to[B])

  def sse[A: Decoder](path: String): Future[Source[A, NotUsed]] = {
    implicit val as: ActorSystem = classicActorSystemProvider.classicSystem
    Http()
      .singleRequest(Get(url(path)))
      .flatMap(Unmarshal(_).to[Source[ServerSentEvent, NotUsed]])
      .map(s =>
        s
          .map(e => e.data)
          .map(data => Json.decode(data.getBytes).to[A].value)
      )
  }
}
