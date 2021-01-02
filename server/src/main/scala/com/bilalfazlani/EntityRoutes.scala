package com.bilalfazlani

import akka.http.scaladsl.marshalling.sse.EventStreamMarshalling._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.sse.ServerSentEvent
import akka.http.scaladsl.server.{Directives, Route}
import akka.stream.scaladsl.Source
import com.bilalfazlani.requests.Add
import com.bilalfazlani.responses.Event
import com.bilalfazlani.responses.Event.Init
import io.bullet.borer.Json
import io.bullet.borer.compat.AkkaHttpCompat

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

class EntityRoutes(ao: EntityActiveObject)(implicit
    ec: ExecutionContext
) extends Directives
    with AkkaHttpCompat {
  def routes: Route =
    (pathPrefix("entities") & get) {
      pathPrefix("subscribe") {
        onSuccess(for {
          data <- ao.getAll
          source <- ao.subscribe()
        } yield (data, source)) { (data, source) =>
          complete(
            Source
              .single(data)
              .map(all => Init(all))
              .map(e =>
                ServerSentEvent(Json.encode(e.asInstanceOf[Event]).toUtf8String)
              )
              .concat(
                source
                  .map(e => ServerSentEvent(Json.encode(e).toUtf8String))
              )
              .keepAlive(3.seconds, () => ServerSentEvent.heartbeat)
          )
        }
      } ~ complete(ao.getAll)
    } ~ pathPrefix("entity") {
      (delete & pathPrefix(IntNumber)) { id =>
        onSuccess(ao.delete(id)) {
          complete(StatusCodes.OK)
        }
      } ~ (post & entity(as[Add])) { add =>
        complete(ao.add(add.name))
      }
    }
}
