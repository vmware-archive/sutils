package com.gopivotal.sutils
package syntax

import scala.util.Try
import scalaz.Validation

class DeserializeOps[A](val self: A) extends AnyVal {
  def deserialize[B](implicit des: Deserialize[A, B]): B =
    des.deserialize(self)

  def deserializeValidation[B](implicit des: Deserialize[A, Validation[Throwable, B]]): Validation[Throwable, B] =
    des.deserialize(self)

  def deserializeTry[B](implicit des: Deserialize[A, Try[B]]): Try[B] =
    des.deserialize(self)
}

trait ToDeserializeOps {
  import scala.language.implicitConversions
  implicit def ToDeserializeOps[A](a: A): DeserializeOps[A] = new DeserializeOps[A](a)
}
