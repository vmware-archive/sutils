package com.gopivotal.sutils

import java.io.{File, FileOutputStream}
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.{Path, Files, StandardOpenOption}

import scala.util.control.NonFatal
import scalaz.{-\/, Show, \/, \/-}

package object io extends IOFunctions with IOInstances {

  // added in 7.1, so use that when update is possible. Can't update cause it breaks jackson...
  private[io] def fromTryCatchNonFatal[A](a: => A): Throwable \/ A = try {
    \/-(a)
  } catch {
    case NonFatal(t) => -\/(t)
  }

  implicit class FileOps(val value: File) extends AnyVal {

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
      fromTryCatchNonFatal {
        value.getParentFile.mkdirs() // in case this doesn't already exist
        close(new FileOutputStream(value)) { os =>
          os.write(data)
          os.flush()
        }
      }

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

}
