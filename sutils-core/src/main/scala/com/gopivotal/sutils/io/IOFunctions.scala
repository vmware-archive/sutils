package com.gopivotal.sutils.io

import java.io.Closeable

trait IOFunctions {
  def close[A <: Closeable, B](a: A)(fn: A => B): B = try {
    fn(a)
  } finally {
    a.close()
  }
}
