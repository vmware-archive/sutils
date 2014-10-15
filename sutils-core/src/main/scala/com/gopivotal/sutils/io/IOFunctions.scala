package com.gopivotal.sutils.io

import java.io.Closeable

trait IOFunctions {
  def close[A <: Closeable, B](a: =>A)(fn: A => B): B = try {
    fn(a)
  } finally {
    a.close()
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
