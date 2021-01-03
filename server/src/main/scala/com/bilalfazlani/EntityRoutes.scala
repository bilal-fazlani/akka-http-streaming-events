package com.bilalfazlani

import akka.http.scaladsl.marshalling.sse.EventStreamMarshalling._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.sse.ServerSentEvent
import akka.http.scaladsl.server.{Directives, Route}
import com.bilalfazlani.requests.Add
import io.bullet.borer.Json
import io.bullet.borer.compat.AkkaHttpCompat

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

class EntityRoutes(ao: EntityActiveObject)(implicit
    ec: ExecutionContext
) extends Directives
    with AkkaHttpCompat {
  def routes: Route = {
    pathPrefix("entities") {
      get {
        //subscribe
        pathPrefix("subscribe") {
          onSuccess(ao.subscribe()) { source =>
            complete(
              source
                .map(e => ServerSentEvent(Json.encode(e).toUtf8String))
                .keepAlive(20.seconds, () => ServerSentEvent.heartbeat)
            )
          }
        } ~
          //get all
          complete(ao.getAll)
      } ~
        delete {
          //delete one
          pathPrefix(IntNumber) { id =>
            onSuccess(ao.delete(id)) {
              complete(StatusCodes.OK)
            }
          } ~
            //delete all
            complete(ao.reset())
        } ~
        //add new
        (post & entity(as[Add])) { add =>
          complete(ao.add(add.name))
        }
    }
  }
}
