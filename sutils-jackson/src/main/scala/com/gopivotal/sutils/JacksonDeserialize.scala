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

trait JacksonDeserializeFunctions {

  import Format._
  import Jackson._

  /**
   * Converts a JSON string into a B
   */
  implicit def jsonDesString[B: Manifest]: Deserialize[StringJSON, B] = new Deserialize[StringJSON, B] {
    override def deserialize(a: StringJSON): B =
      mapper.readValue[B](a)
  }

  /**
   * Converts a JSON byte array into a B
   */
  implicit def jsonDesBytes[B: Manifest]: Deserialize[BytesJSON, B] = new Deserialize[BytesJSON, B] {
    override def deserialize(a: BytesJSON): B =
      mapper.readValue[B](a)
  }

  /**
   * Converts a JSON file into a B
   */
  implicit def jsonDesFile[B: Manifest]: Deserialize[FileJSON, B] = new Deserialize[FileJSON, B] {
    override def deserialize(a: FileJSON): B =
      mapper.readValue[B](a)
  }

  /**
   * Converts a YAML string into a B
   */
  implicit def yamlDesString[B: Manifest]: Deserialize[StringYAML, B] = new Deserialize[StringYAML, B] {
    override def deserialize(a: StringYAML): B =
      mapper.readValue[B](yamlFactory.createParser(a))
  }

  /**
   * Converts a YAML byte array into a B
   */
  implicit def yamlDesBytes[B: Manifest]: Deserialize[BytesYAML, B] = new Deserialize[BytesYAML, B] {
    override def deserialize(a: BytesYAML): B =
      mapper.readValue[B](yamlFactory.createParser(a))
  }

  /**
   * Converts a YAML file into a B
   */
  implicit def yamlDesFile[B: Manifest]: Deserialize[FileYAML, B] = new Deserialize[FileYAML, B] {
    override def deserialize(a: FileYAML): B =
      mapper.readValue[B](yamlFactory.createParser(a))
  }

}

object JacksonDeserialize extends JacksonDeserializeFunctions
