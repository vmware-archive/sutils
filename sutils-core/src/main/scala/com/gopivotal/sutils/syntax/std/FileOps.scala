package com.gopivotal.sutils.syntax.std

import java.io.File

import com.gopivotal.sutils.io.FileOps

import scala.io.{Source, BufferedSource, Codec}

trait ToFileOps extends Any {
  import scala.language.implicitConversions
  implicit def ToFileOps(file: File): FileOps = new FileOps(file)
}