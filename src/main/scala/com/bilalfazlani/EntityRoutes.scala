package com.bilalfazlani

import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import akka.http.scaladsl.server.{Directives, Route}
import com.bilalfazlani.requests.Add
import io.bullet.borer.compat.AkkaHttpCompat

class EntityRoutes(ao: EntityActiveObject)
    extends Directives
    with AkkaHttpCompat {
  def routes: Route =
    (pathPrefix("entities") & get) {
      complete(ao.getAll)
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
