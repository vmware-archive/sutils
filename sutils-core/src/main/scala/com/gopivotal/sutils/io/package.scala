package com.gopivotal.sutils

import java.io.{FileOutputStream, File}
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.{Files, StandardOpenOption}

import scala.util.control.NonFatal
import scalaz.{-\/, \/-, Show, \/}

package object io extends IOFunctions {

  // added in 7.1, so use that when update is possible. Can't update cause it breaks jackson...
  private[io] def fromTryCatchNonFatal[A](a: => A): Throwable \/ A = try {
    \/-(a)
  } catch {
    case NonFatal(t) => -\/(t)
  }

  implicit class FileOps(val value: File) extends AnyVal {
    def /(child: String): File =
      new File(value, child)

    def /[A: Show](child: A): File =
      new File(value, Show[A].shows(child))

    def listChildren(): Option[Seq[File]] =
      Option(value.listFiles()).map(Seq(_: _*))

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

    def write(data: ByteBuffer): Throwable \/ Unit =
      fromTryCatchNonFatal {
        value.getParentFile.mkdirs() // in case this doesn't already exist
        close(FileChannel.open(value.toPath, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)) { c =>
          while (data.hasRemaining) c.write(data)
          c.force(true)
        }
      }
  }

}
