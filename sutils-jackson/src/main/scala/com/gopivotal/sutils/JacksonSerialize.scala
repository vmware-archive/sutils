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

import java.io.{ByteArrayOutputStream, File, FileOutputStream, StringWriter}

trait JacksonSerializeFunctions {

  import Format._
  import Jackson._

  /**
   * Serializes A into a JSON string
   */
  implicit def jsonSerString[A: Manifest]: Serialize[A, StringJSON] = new Serialize[A, StringJSON] {
    override def serialize(a: A): StringJSON =
      JSON(mapper.writerWithType[A].writeValueAsString(a))
  }

  /**
   * Serializes A into a JSON byte array
   */
  implicit def jsonSerBytes[A: Manifest]: Serialize[A, BytesJSON] = new Serialize[A, BytesJSON] {
    override def serialize(a: A): BytesJSON =
      JSON(mapper.writerWithType[A].writeValueAsBytes(a))
  }

  /**
   * Serializes A into a JSON file
   */
  implicit def jsonSerFile[A: Manifest]: Serialize[A, SerializeTo[File, JSON]] = new Serialize[A, SerializeTo[File, JSON]] {
    override def serialize(a: A): SerializeTo[File, JSON] = new SerializeTo[File, JSON] {
      override def to(c: File): Unit = mapper.writerWithType[A].writeValue(c, a)
    }
  }

  /**
   * Serializes A into a YAML string
   */
  implicit def yamlSerString[A: Manifest]: Serialize[A, StringYAML] = new Serialize[A, StringYAML] {
    override def serialize(t: A): StringYAML = {
      val sw = new StringWriter()
      mapper.writerWithType[A].writeValue(yamlFactory.createGenerator(sw), t)
      YAML(sw.toString)
    }
  }

  /**
   * Serializes A into a YAML byte array
   */
  implicit def yamlSerBytes[A: Manifest]: Serialize[A, BytesYAML] = new Serialize[A, BytesYAML] {
    override def serialize(t: A): BytesYAML = {
      val os = new ByteArrayOutputStream()
      mapper.writerWithType[A].writeValue(yamlFactory.createGenerator(os), t)
      YAML(os.toByteArray)
    }
  }

  /**
   * Serializes A into a YAML file
   */
  implicit def yamlSerFile[A: Manifest]: Serialize[A, SerializeTo[File, YAML]] = new Serialize[A, SerializeTo[File, YAML]] {
    override def serialize(a: A): SerializeTo[File, YAML] = new SerializeTo[File, YAML] {
      override def to(c: File): Unit = {
        if (!c.getParentFile.exists()) {
          c.getParentFile.mkdirs()
        }
        val os = new FileOutputStream(c)
        try {
          mapper.writerWithType[A].writeValue(yamlFactory.createGenerator(os), a)
        } finally {
          os.close()
        }
      }
    }
  }
}

object JacksonSerialize extends JacksonSerializeFunctions
