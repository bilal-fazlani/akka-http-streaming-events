package com

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}

package object bilalfazlani {
  implicit class FutureExtension[T](f: Future[T]) {
    def block: T = Await.result(f, 5.seconds)
  }
}
