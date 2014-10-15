package com.gopivotal.sutils.io

import java.io.Closeable

trait IOFunctions { self =>
  def close[A <: Closeable, B](a: => A)(fn: A => B): B = {
    lazy val alocal = a
    try {
      fn(alocal)
    } finally {
      alocal.close()
    }
  }

  def close[A <: Closeable, B <: Closeable, C](a: => A, b: => B)(fn: (A, B) => C): C = {
    lazy val alocal = a
    lazy val blocal = b
    try {
      fn(alocal, blocal)
    } finally {
      try {
        alocal.close()
      } finally {
        blocal.close()
      }
    }
  }
}
