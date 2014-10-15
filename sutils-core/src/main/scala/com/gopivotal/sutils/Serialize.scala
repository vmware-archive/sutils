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

import java.nio.ByteBuffer

import scala.util.Try
import scala.util.control.NonFatal
import scalaz.{\/-, -\/, \/, Validation}

/**
 * Serialize typeclass is for converting one type into another.  The main conversions are into data types such as
 * JSON and YAML, but other data formats can be added
 *
 * @tparam A Input data type
 * @tparam B Output data type
 */
trait Serialize[A, B] {self =>
  def serialize(a: A): B
}

/**
 * Lazy version of Serialize.  This is mainly used as a way to imperatively serialize to external types; most common
 * usage is to stream serialized results to disk.
 *{{{
 *   scala> import java.io.File
 *   scala> import com.gopivotal.sutils.SerializeTo
 *   scala> import com.gopivotal.sutils.syntax.serialize._
 *   scala> import com.gopivotal.sutils.JacksonSerialize._
 *   scala> import com.gopivotal.sutils.Format
 *   scala> case class Person(name: String, age: Int)
 *   scala> val person = Person("Bob", 27)
 *   scala> person.serialize[SerializeTo[File, Format.YAML]] to new File("/tmp/person")
 *
 *   $ cat /tmp/person
 *   ---
 *   name: "Bob"
 *   age: 27
 *}}}
 * @tparam C to serialize to
 * @tparam D data format
 */
trait SerializeTo[C, D] {self =>
  def to(c: C): Unit
}

trait SerializeFunctions {self =>

  /**
   * Serializer that wraps calls around a validation.  All failures return Failure
   */
  implicit def validateSerialize[A, B](implicit ser: Serialize[A, B]): Serialize[A, Validation[Throwable, B]] =
    new Serialize[A, Validation[Throwable, B]] {
      override def serialize(a: A): Validation[Throwable, B] = Validation.fromTryCatch(ser.serialize(a))
    }

  /**
   * Serializer that wraps calls around a try.  All failures return Failure
   */
  implicit def trySerialize[A, B](implicit ser: Serialize[A, B]): Serialize[A, Try[B]] =
    new Serialize[A, Try[B]] {
      override def serialize(a: A): Try[B] = Try(ser.serialize(a))
    }

  /**
   * Serializer that uses either to denote failure or success.  Only [[NonFatal]] exceptions are returned.
   */
  implicit def scalazEitherSerialize[A, B](implicit ser: Serialize[A, B]): Serialize[A, Throwable \/ B] =
    new Serialize[A, Throwable \/ B] {
      override def serialize(a: A): Throwable \/ B =
        try {
          \/-(ser.serialize(a))
        } catch {
          case NonFatal(t) => -\/(t)
        }
    }

  implicit def byteSerializerToByteBuffer[A](implicit as: Serialize[A, Array[Byte]]): Serialize[A, ByteBuffer] =
    Serialize.serialize(a => ByteBuffer.wrap(as.serialize(a)))

  implicit def identitySerializer[A]: Serialize[A, A] =
    Serialize.serialize(identity)
}

/**
 * Functions and instances for working with Serialize typeclass.  The main usage is to import the functions defined.
 */
object Serialize extends SerializeFunctions {

  @inline def apply[A, B](implicit s: Serialize[A, B]): Serialize[A, B] = s

  def serialize[A, B](fn: A => B): Serialize[A, B] = new Serialize[A, B] {
    override def serialize(a: A): B = fn(a)
  }
}