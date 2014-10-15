/**
 *
 * Copyright (C) 2013-2014 Pivotal Software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the under the Apache License,
 * Version 2.0 (the "License”); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gopivotal

import scala.annotation.tailrec

package object sutils {

  /**
   * This is a "case function", which is just a type alias around [[PartialFunction]] giving shorter form.
   *
   * Example usage
   * {{{
   *   val o: Option[String] =>? Int = {
   *    case Some(a) => a.length
   *    case None => 0
   *   }
   * }}}
   *
   * This syntax came from http://eed3si9n.com/scala-the-flying-sandwich-parts.
   */
  type =>?[-A, +B] = PartialFunction[A, B]

  /**
   * Runs a function until a given precondition is met.
   *
   * @return last value that the precondition approved
   */
  @tailrec
  def doWhile[A](fn: () => A)(p: A => Boolean): A = {
    val x = fn()
    if (p(x)) doWhile(fn)(p) else x
  }
}
