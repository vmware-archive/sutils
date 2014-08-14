package com.gopivotal.sutils.syntax.std

import java.io.File

import scala.io.{Source, BufferedSource, Codec}

/**
 * Extension methods for [[File]]
 */
final class FileOps(val self: File) extends AnyVal with ToFileOps {

  /**
   * Creates a new [[File]] object from the parent and child paths
   */
  def /(child: String): File = new File(self, child)

  /**
   * Tries to delete all [[File]]s under this path.  Returns true if able to delete this [[File]]
   */
  def deleteAll: Boolean = {
    if (self.isDirectory) for {p: File <- self.listFiles()} p.deleteAll
    self.delete()
  }

  /**
   * Lifts the [[File]] into an [[Option]].  If {{{file.exists}}} is false, then [[None]] is returned, else [[Some(self)]
   * @return
   */
  def toOption: Option[File] =
    if(self.exists()) Some(self) else None

  /**
   * Creates a list of all children under this path.
   *
   * This method wraps {{{self.listFiles()}}} from the java API.  The main reason to use this over java's is that
   * when a path does not contain children, java returns null where as this call returns an empty [[List]]
   */
  def children: List[File] = self.listFiles() match {
    case null => List()
    case e => e.toList
  }

  /**
   * Creates a [[Source]] from the given [[File]] and [[Codec]].  This call delegates to {{{Source.fromFile(self)}}} so the
   * default buffer size is Source.DefaultBufSize

   * @param codec format the file is in.  Normally {{{Codec.UTF8}}} for text files
   */
  def source(implicit codec: Codec): BufferedSource =
    Source.fromFile(self)

  /**
   * Creates a [[Source]] from the given [[File]]
   *
   * @param bufferSize how much memory to allocate for reading from the file
   * @param codec format the file is in.  Normally {{{Codec.UTF8}}} for text files
   */
  def source(bufferSize: Int)(implicit codec: Codec): BufferedSource =
    Source.fromFile(self, bufferSize)
}

trait ToFileOps extends Any {
  implicit def ToFileOps(file: File): FileOps = new FileOps(file)
}