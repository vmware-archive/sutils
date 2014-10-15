package com.gopivotal.sutils
package io

import java.io.{BufferedInputStream, File, FileInputStream, InputStream}
import java.nio.ByteBuffer
import java.nio.file.Files

import scalaz.Equal

trait IOInstances { self =>
  private[io] val BufferSize = 4096

  implicit val ByteBufferEqual: Equal[ByteBuffer] =
    Equal.equalA[ByteBuffer]

  implicit val FileByteEqual: Equal[File] = Equal.equal { (l, r) =>
    // if length doesn't match, why bother?
    if (l.isDirectory && r.isDirectory) {
      // follow symlink and then compare path
      Files.readSymbolicLink(l.toPath) == Files.readSymbolicLink(r.toPath)
    } else if (l.length() == r.length()) {
      def wrap(file: File): InputStream =
        new BufferedInputStream(new FileInputStream(file))

      def buffer(is: InputStream): Iterator[Stream[Byte]] =
        Stream.continually(is.read).takeWhile(_ != -1).map(_.toByte).grouped(BufferSize)

      close(wrap(l), wrap(r)) { (lis, ris) =>
        buffer(lis).zip(buffer(ris)).foldLeft(true) { (accum, v) =>
          val eq = v._1 == v._2
          accum && eq
        }
      }
    } else false
  }
}
