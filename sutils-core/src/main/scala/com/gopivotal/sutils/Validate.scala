/**
 *
 * Copyright (C) 2013-2014 Pivotal Software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the under the Apache License,
 * Version 2.0 (the "License”); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gopivotal.sutils

/**
 * Typeclass for handling validating an object.  Normally the B case should explain if A is valid or not.  B is normally
 * implementation specific.
 */
trait Validate[A, B] { self =>
  def validate(a: A): B
}

trait ValidateFunctions { self =>
  import scalaz.Scalaz._
  import scalaz._

  /**
   * Validates a [[List]] of [[A]], aggregating their errors
   */
  implicit def listValidate[E: Semigroup, A](implicit v: Validate[A, Validation[E, A]]): Validate[List[A], Validation[E, List[A]]] =
    new Validate[List[A], Validation[E, List[A]]] {
      override def validate(ax: List[A]): Validation[E, List[A]] =
//        ax.map(v.validate).sequenceU // intellij can't figure this one out
        ax.map(v.validate).sequence[({type V[X] = Validation[E, X]})#V, A]
    }
}

/**
 * Functions and instances for working with Validate typeclass.  The main usage is to import the functions defined.
 */
object Validate extends ValidateFunctions {

  @inline def apply[A, B](implicit v: Validate[A, B]): Validate[A, B] = v

  /**
   * Constructs a new [[Validate]] based off the given function
   */
  def validate[A, B](fn: A => B) = new Validate[A, B] {
    override def validate(a: A): B = fn(a)
  }
}
