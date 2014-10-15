package com.gopivotal.sutils

import java.nio.ByteBuffer
import java.nio.charset.Charset

trait BinarySerializeInstances { self =>
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

trait BinarySerializeFunctions { self =>
  implicit def stringSer(implicit c: Charset = Charset.forName("UTF-8")): Serialize[String, Array[Byte]] =
    Serialize.serialize(_.getBytes(c))
}

object BinarySerialize extends BinarySerializeInstances with BinarySerializeFunctions
