/**
 *
 * Copyright (C) 2013-2014 Pivotal Software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the under the Apache License,
 * Version 2.0 (the "License”); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gopivotal.sutils

import scala.util.Try
import scalaz.Validation

/**
 * Deserialize typeclass is for converting one type into another.  The main conversions are from data types such as
 * JSON and YAML, but other data formats can be added
 *
 * @tparam A Input data type
 * @tparam B Output data type
 */
trait Deserialize[A, B] {self =>
  def deserialize(a: A): B
}

trait DeserializeFunctions { self =>

  /**
   * Converts A into a validation of B.  If any exception are thrown, then Failure(e) is returned
   */
  implicit def validateDeserialize[A, B](implicit des: Deserialize[A, B]): Deserialize[A, Validation[Throwable, B]] =
    new Deserialize[A, Validation[Throwable, B]] {
      override def deserialize(a: A): Validation[Throwable, B] =
        Validation.fromTryCatch(des.deserialize(a))
    }

  /**
   * Converts A into try of B.  If any exceptions are thrown, then Failure(e) is returned
   */
  implicit def tryDeserialize[A, B](implicit des: Deserialize[A, B]): Deserialize[A, Try[B]] =
    new Deserialize[A, Try[B]] {
      override def deserialize(a: A): Try[B] =
        Try(des.deserialize(a))
    }
}

/**
 * Functions and instances for working with Deserialize typeclass.  The main usage is to import the functions defined.
 */
object Deserialize extends DeserializeFunctions {

  @inline def apply[A, B](implicit d: Deserialize[A, B]): Deserialize[A, B] = d

  /**
   * Constructs a new [[Deserialize]] based off the given function
   */
  def deserialize[A, B](fn: A => B): Deserialize[A, B] = new Deserialize[A, B] {
    override def deserialize(a: A): B = fn(a)
  }
}
