package com.gopivotal.sutils
package syntax

import scala.util.Try
import scalaz.Validation

class SerializeOps[A](val a: A) extends AnyVal {

  /**
   * Serialize this object into B
   */
  def serialize[B](implicit ser: Serialize[A, B]): B =
    ser.serialize(a)

  /**
   * Serialize this object into a B wrapped around validation.  If the serialize fails, then Failure(e) is returned
   */
  def serializeValidation[B](implicit ser: Serialize[A, Validation[Throwable, B]]): Validation[Throwable, B] =
    ser.serialize(a)

  /**
   * Serialize this object into a B wrapped around try.  If the serialize fails, then Failure(e) is returned
   */
  def serializeTry[B](implicit ser: Serialize[A, Try[B]]): Try[B] =
    ser.serialize(a)
}

trait ToSerializeOps {
  import scala.language.implicitConversions
  implicit def ToSerializeOps[A](a: A): SerializeOps[A] = new SerializeOps[A](a)
}
