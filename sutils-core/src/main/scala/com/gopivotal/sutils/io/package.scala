package com.gopivotal.sutils

import scala.util.control.NonFatal
import scalaz.{-\/, \/, \/-}

package object io extends IOFunctions with IOInstances {

  // added in 7.1, so use that when update is possible. Can't update cause it breaks jackson...
  private[io] def fromTryCatchNonFatal[A](a: => A): Throwable \/ A = try {
    \/-(a)
  } catch {
    case NonFatal(t) => -\/(t)
  }
}
