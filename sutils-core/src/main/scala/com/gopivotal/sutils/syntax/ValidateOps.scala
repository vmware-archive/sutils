package com.gopivotal.sutils
package syntax

class ValidateOpt[A](val self: A) extends AnyVal {

  /**
   * Validates [[self]] based off the implicit [[Validate]] defined
   */
  def validate[B](implicit v: Validate[A, B]): B =
    v.validate(self)
}

trait ToValidateOpt {
  import scala.language.implicitConversions
  implicit def ToValidateOpt[A](a: A): ValidateOpt[A] = new ValidateOpt[A](a)
}
