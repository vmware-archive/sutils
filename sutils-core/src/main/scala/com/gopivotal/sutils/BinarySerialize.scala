package com.gopivotal.sutils

import java.nio.ByteBuffer
import java.nio.charset.Charset

/**
 * [[Serialize]] instances that convert to binary data sets.
 */
trait BinarySerializeInstances { self =>

  implicit val ByteSerialize: Serialize[Byte, ByteBuffer] =
    Serialize.serialize(a => ByteBuffer.allocate(1).put(a))

  implicit val CharSerialize: Serialize[Char, ByteBuffer] =
    Serialize.serialize(a => ByteBuffer.allocate(2).putChar(a))

  implicit val ShortSerialize: Serialize[Short, ByteBuffer] =
    Serialize.serialize(a => ByteBuffer.allocate(2).putShort(a))

  implicit val IntSerialize: Serialize[Int, ByteBuffer] =
    Serialize.serialize(a => ByteBuffer.allocate(4).putInt(a))

  implicit val FloatSerialize: Serialize[Float, ByteBuffer] =
    Serialize.serialize(a => ByteBuffer.allocate(4).putFloat(a))

  implicit val LongSerialize: Serialize[Long, ByteBuffer] =
    Serialize.serialize(a => ByteBuffer.allocate(8).putLong(a))


  implicit val DoubleSerialize: Serialize[Double, ByteBuffer] =
    Serialize.serialize(a => ByteBuffer.allocate(8).putDouble(a))
}

/**
 * Functions for converting data using [[Serialize]] into binary data.
 */
trait BinarySerializeFunctions { self =>

  /**
   * Converts a string into binary data.  Uses [[Charset]] for this process, and will default to UTF-8.
   */
  implicit def stringSerialize(implicit c: Charset = Charset.forName("UTF-8")): Serialize[String, Array[Byte]] =
    Serialize.serialize(_.getBytes(c))
}

object BinarySerialize extends BinarySerializeInstances with BinarySerializeFunctions
