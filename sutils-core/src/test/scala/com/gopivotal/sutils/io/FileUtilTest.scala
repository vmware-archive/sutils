package com.gopivotal.sutils.io

import java.io.File
import java.nio.ByteBuffer

import com.gopivotal.sutils.BaseTest
import com.gopivotal.sutils.syntax.serialize._
import com.gopivotal.sutils.BinarySerialize._

import scalaz.{Equal, \/-, -\/, Show}

class FileUtilTest extends BaseTest {
  "create new paths from string" in {
    val path = new File("/") / "bar" / "baz" / "biz"
    path shouldBe new File("/bar/baz/biz")
  }

  "create new path from shows" in {
    case class Foo(name: String)
    implicit val FooShow = Show.shows[Foo](_.name)

    val path = new File("/") / Foo("bar") / Foo("baz") / Foo("biz")
    path shouldBe new File("/bar/baz/biz")
  }

  "transferTo creates full copy" in {
    val base = createTmp()

    val src = base / "src"
    val dest = base / "dest"

    // random stuff from scala logs, so why not use it?
    src.write(
      """
        |Warning:scalac: Unit <: scalaz.Monoid[scalaz.Validation[E,X]]?
        |Warning:scalac:
        |Warning:scalac: false
        |Warning:scalac: com.gopivotal.sutils.Serialize[java.io.Serializable,java.io.Serializable] <: com.gopivotal.sutils.Serialize[com.gopivotal.sutils.DeserializeTest.PuppetCommand,com.gopivotal.sutils.Format.StringJSON]?
        |Warning:scalac: com.gopivotal.sutils.Serialize[java.io.Serializable,java.io.Serializable] <: com.gopivotal.sutils.Serialize[com.gopivotal.sutils.DeserializeTest.PuppetCommand,com.gopivotal.sutils.Format.BytesJSON]?
        |Warning:scalac: com.gopivotal.sutils.Serialize[Object,Object] <: com.gopivotal.sutils.Serialize[List[com.gopivotal.sutils.DeserializeTest.Command],com.gopivotal.sutils.Format.StringJSON]?
        |Warning:scalac: com.gopivotal.sutils.Serialize[Object,Object] <: com.gopivotal.sutils.Serialize[List[com.gopivotal.sutils.DeserializeTest.Command],com.gopivotal.sutils.Format.BytesJSON]?
        |Warning:scalac: com.gopivotal.sutils.Serialize[java.io.Serializable,java.io.Serializable] <: com.gopivotal.sutils.Serialize[com.gopivotal.sutils.DeserializeTest.PuppetCommand,com.gopivotal.sutils.Format.StringYAML]?
        |Warning:scalac: com.gopivotal.sutils.Serialize[java.io.Serializable,java.io.Serializable] <: com.gopivotal.sutils.Serialize[com.gopivotal.sutils.DeserializeTest.PuppetCommand,com.gopivotal.sutils.Format.BytesYAML]?
        |Warning:scalac: com.gopivotal.sutils.Serialize[Object,Object] <: com.gopivotal.sutils.Serialize[List[com.gopivotal.sutils.DeserializeTest.Command],com.gopivotal.sutils.Format.StringYAML]?
      """.serialize[ByteBuffer])

    src.transferTo(dest) match {
      case -\/(t) => fail(t)
      case \/-(l) => l shouldBe src.length()
    }
  }

  "same file are Equal" in {
    val base = createTmp()

    val src = base / "src"
    val dest = base / "dest"

    // random stuff from scala logs, so why not use it?
    src.write(
      """
        |Warning:scalac: Unit <: scalaz.Monoid[scalaz.Validation[E,X]]?
        |Warning:scalac:
        |Warning:scalac: false
        |Warning:scalac: com.gopivotal.sutils.Serialize[java.io.Serializable,java.io.Serializable] <: com.gopivotal.sutils.Serialize[com.gopivotal.sutils.DeserializeTest.PuppetCommand,com.gopivotal.sutils.Format.StringJSON]?
        |Warning:scalac: com.gopivotal.sutils.Serialize[java.io.Serializable,java.io.Serializable] <: com.gopivotal.sutils.Serialize[com.gopivotal.sutils.DeserializeTest.PuppetCommand,com.gopivotal.sutils.Format.BytesJSON]?
        |Warning:scalac: com.gopivotal.sutils.Serialize[Object,Object] <: com.gopivotal.sutils.Serialize[List[com.gopivotal.sutils.DeserializeTest.Command],com.gopivotal.sutils.Format.StringJSON]?
        |Warning:scalac: com.gopivotal.sutils.Serialize[Object,Object] <: com.gopivotal.sutils.Serialize[List[com.gopivotal.sutils.DeserializeTest.Command],com.gopivotal.sutils.Format.BytesJSON]?
        |Warning:scalac: com.gopivotal.sutils.Serialize[java.io.Serializable,java.io.Serializable] <: com.gopivotal.sutils.Serialize[com.gopivotal.sutils.DeserializeTest.PuppetCommand,com.gopivotal.sutils.Format.StringYAML]?
        |Warning:scalac: com.gopivotal.sutils.Serialize[java.io.Serializable,java.io.Serializable] <: com.gopivotal.sutils.Serialize[com.gopivotal.sutils.DeserializeTest.PuppetCommand,com.gopivotal.sutils.Format.BytesYAML]?
        |Warning:scalac: com.gopivotal.sutils.Serialize[Object,Object] <: com.gopivotal.sutils.Serialize[List[com.gopivotal.sutils.DeserializeTest.Command],com.gopivotal.sutils.Format.StringYAML]?
      """.serialize[ByteBuffer])

    Equal[File].equal(src, src) shouldBe true
  }

  "transferTo creates equal files" in {
    val base = createTmp()

    val src = base / "src"
    val dest = base / "dest"

    // random stuff from scala logs, so why not use it?
    src.write(
      """
        |Warning:scalac: Unit <: scalaz.Monoid[scalaz.Validation[E,X]]?
        |Warning:scalac:
        |Warning:scalac: false
        |Warning:scalac: com.gopivotal.sutils.Serialize[java.io.Serializable,java.io.Serializable] <: com.gopivotal.sutils.Serialize[com.gopivotal.sutils.DeserializeTest.PuppetCommand,com.gopivotal.sutils.Format.StringJSON]?
        |Warning:scalac: com.gopivotal.sutils.Serialize[java.io.Serializable,java.io.Serializable] <: com.gopivotal.sutils.Serialize[com.gopivotal.sutils.DeserializeTest.PuppetCommand,com.gopivotal.sutils.Format.BytesJSON]?
        |Warning:scalac: com.gopivotal.sutils.Serialize[Object,Object] <: com.gopivotal.sutils.Serialize[List[com.gopivotal.sutils.DeserializeTest.Command],com.gopivotal.sutils.Format.StringJSON]?
        |Warning:scalac: com.gopivotal.sutils.Serialize[Object,Object] <: com.gopivotal.sutils.Serialize[List[com.gopivotal.sutils.DeserializeTest.Command],com.gopivotal.sutils.Format.BytesJSON]?
        |Warning:scalac: com.gopivotal.sutils.Serialize[java.io.Serializable,java.io.Serializable] <: com.gopivotal.sutils.Serialize[com.gopivotal.sutils.DeserializeTest.PuppetCommand,com.gopivotal.sutils.Format.StringYAML]?
        |Warning:scalac: com.gopivotal.sutils.Serialize[java.io.Serializable,java.io.Serializable] <: com.gopivotal.sutils.Serialize[com.gopivotal.sutils.DeserializeTest.PuppetCommand,com.gopivotal.sutils.Format.BytesYAML]?
        |Warning:scalac: com.gopivotal.sutils.Serialize[Object,Object] <: com.gopivotal.sutils.Serialize[List[com.gopivotal.sutils.DeserializeTest.Command],com.gopivotal.sutils.Format.StringYAML]?
      """.serialize[ByteBuffer])

    src.transferTo(dest) match {
      case -\/(t) => fail(t)
      case \/-(l) => l shouldBe src.length()
    }

    Equal[File].equal(src, dest) shouldBe true
  }

  private[this] def createTmp(): File = {
    val file = File.createTempFile("FileUtilTest", System.nanoTime().toString)
    assert(file.delete(), "Unable to delete tmp dir before test")
    file.mkdir()
    file
  }
}
