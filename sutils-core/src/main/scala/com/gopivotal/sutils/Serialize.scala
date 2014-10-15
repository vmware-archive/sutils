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
import java.nio.charset.Charset

import scala.util.Try
import scalaz.Validation

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
 *
 * @tparam C to serialize to
 * @tparam D data format
 */
trait SerializeTo[C, D] {self =>
  def to(c: C): Unit
}

trait SerializeInstances { self =>
  implicit val charSer: Serialize[Char, ByteBuffer] =
    Serialize.serialize(a => ByteBuffer.allocate(1).putChar(a))

  implicit val byteSer: Serialize[Byte, ByteBuffer] =
    Serialize.serialize(a => ByteBuffer.allocate(1).put(a))

  implicit val shortSer: Serialize[Short, ByteBuffer] =
    Serialize.serialize(a => ByteBuffer.allocate(2).putShort(a))

  implicit val intSer: Serialize[Int, ByteBuffer] =
    Serialize.serialize(a => ByteBuffer.allocate(4).putInt(a))

  implicit val longSer: Serialize[Long, ByteBuffer] =
    Serialize.serialize(a => ByteBuffer.allocate(8).putLong(a))

  implicit val floatSer: Serialize[Float, ByteBuffer] =
    Serialize.serialize(a => ByteBuffer.allocate(4).putFloat(a))

  implicit val doubleSer: Serialize[Double, ByteBuffer] =
    Serialize.serialize(a => ByteBuffer.allocate(8).putDouble(a))
}

trait SerializeFunctions { self =>

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

  implicit def stringSer(implicit c: Charset): Serialize[String, Array[Byte]] =
    Serialize.serialize(_.getBytes(c))

  implicit def byteSerializerToByteBuffer[A](implicit as: Serialize[A, Array[Byte]]): Serialize[A, ByteBuffer] =
    Serialize.serialize(a => ByteBuffer.wrap(as.serialize(a)))

  implicit def identitySerializer[A]: Serialize[A, A] =
    Serialize.serialize(identity)
}

/**
 * Functions and instances for working with Serialize typeclass.  The main usage is to import the functions defined.
 */
object Serialize extends SerializeFunctions with SerializeInstances {

  @inline def apply[A, B](implicit s: Serialize[A, B]): Serialize[A, B] = s

  def serialize[A, B](fn: A => B): Serialize[A, B] = new Serialize[A, B] {
    override def serialize(a: A): B = fn(a)
  }
}