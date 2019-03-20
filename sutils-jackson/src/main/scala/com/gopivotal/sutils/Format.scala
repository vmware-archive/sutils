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
package com.gopivotal.sutils

import java.io.File

import scalaz.{@@, Tag}

trait FormatFunctions {
  trait JSON

  trait YAML

  type BytesYAML = Array[Byte] @@ YAML
  type StringYAML = String @@ YAML
  type FileYAML = File @@ YAML

  type BytesJSON = Array[Byte] @@ JSON
  type StringJSON = String @@ JSON
  type FileJSON = File @@ JSON

  def JSON[A](a: A): A @@ JSON = Tag[A, JSON](a)

  def YAML[A](a: A): A @@ YAML = Tag[A, YAML](a)
}

/**
 * Data formats commonly used with serialization.  These work with tag-types, for details on these, go
 * to https://dcapwell.github.io/scala-tour/Tag%20Types.html
 */
object Format extends FormatFunctions
