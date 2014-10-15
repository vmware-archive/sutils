package com.gopivotal.sutils.io

import java.io.{FileOutputStream, File}
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.{Path, Files}

import com.gopivotal.sutils.syntax.std.ToFileOps

import scalaz.{\/, Show}

final class FileOps(val value: File) extends AnyVal with ToFileOps {

  import java.nio.file.StandardOpenOption._

  def /(child: String): File =
    new File(value, child)

  def /[A: Show](child: A): File =
    new File(value, Show[A].shows(child))

  def deleteAll: Boolean = {
    if (value.isDirectory) for {p: File <- value.children()} p.deleteAll
    value.delete()
  }

  def toOption: Option[File] =
    if (value.exists()) Some(value) else None

  def children(): Seq[File] =
    value.listFiles() match {
      case null => Seq()
      case c => Seq(c: _*)
    }

  def readFully(): Throwable \/ Array[Byte] =
    fromTryCatchNonFatal(Files.readAllBytes(value.toPath))

  def write(data: Array[Byte]): Throwable \/ Unit =
    value.write(data, 0, data.length)

  def write(data: Array[Byte], offset: Int, length: Int): Throwable \/ Unit =
    fromTryCatchNonFatal {
      value.getParentFile.mkdirs() // in case this doesn't already exist
      close(new FileOutputStream(value)) { os =>
        os.write(data, offset, length)
        os.flush()
      }
    }

  def write(data: ByteBuffer): Throwable \/ Unit =
    fromTryCatchNonFatal {
      value.getParentFile.mkdirs() // in case this doesn't already exist
      close(FileChannel.open(value.toPath, CREATE_NEW, WRITE)) { c =>
        while (data.hasRemaining) c.write(data)
        c.force(true)
      }
    }

  def transferTo(target: File): Throwable \/ Long =
    value.transferTo(target.toPath)

  def transferTo(target: Path): Throwable \/ Long =
    fromTryCatchNonFatal {
      close(FileChannel.open(value.toPath, READ), FileChannel.open(target, CREATE_NEW, WRITE)) { (src, dest) =>
        src.transferTo(0, value.length(), dest)
      }
    }
}
