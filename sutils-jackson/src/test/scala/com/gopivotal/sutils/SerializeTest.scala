/**
 *
 * Copyright (C) 2013-2014 Pivotal Software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the under the Apache License,
 * Version 2.0 (the "License”); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gopivotal.sutils

import com.fasterxml.jackson.annotation.JsonSubTypes.Type
import com.fasterxml.jackson.annotation.{JsonSubTypes, JsonTypeInfo, JsonTypeName}
import org.slf4j.LoggerFactory

object SerializeTest {

  case class Orchestration(module: String,
                           version: String,
                           description: String,
                           actions: List[Action],
                           roles: List[String])

  case class Action(name: String,
                    description: String,
                    commands: List[Command])

  @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
  @JsonSubTypes(Array(
    new Type(classOf[ServiceCommand]),
    new Type(classOf[PuppetCommand])
  ))
  trait Command

  @JsonTypeName("service")
  case class ServiceCommand(name: String,
                            args: String,
                            classFilter: String,
                            factFilter: String) extends Command

  @JsonTypeName("puppet")
  case class PuppetCommand(resource: String,
                           classFilter: String,
                           factFilter: String) extends Command

}

class SerializeTest extends BaseTest {

  import SerializeTest._
  import com.gopivotal.sutils.Serialize._
  import com.gopivotal.sutils.Format._
  import com.gopivotal.sutils.JacksonSerialize._
  import com.gopivotal.sutils.syntax.serialize._

  val logger = LoggerFactory.getLogger(getClass.getName)

  "json serialize on case class should return json response" in {
    val command = PuppetCommand("hadoop::namenode", "hadoop::namenode", "")

    val json = command.serialize[StringJSON]
    logger.info("Command JSON: {}", json)

    val expectedJson = """{"type":"puppet","resource":"hadoop::namenode","classFilter":"hadoop::namenode","factFilter":""}"""

    //TODO this is order dependent, but JSON isn't
    json shouldBe expectedJson
  }

  "json serialize on bytes on case class should return json response in bytes" in {
    val command = PuppetCommand("hadoop::namenode", "hadoop::namenode", "")

    val json = command.serialize[BytesJSON]
    logger.info("Command JSON: {}", new String(json))

    val expectedJson = """{"type":"puppet","resource":"hadoop::namenode","classFilter":"hadoop::namenode","factFilter":""}"""

    //TODO this is order dependent, but JSON isn't
    json shouldBe expectedJson.getBytes()
  }

  "yaml serialize on case class should return yaml response" in {
    val command = PuppetCommand("hadoop::namenode", "hadoop::namenode", "")

    val yaml = command.serialize[StringYAML]
    logger.info("Command YAML: {}", yaml)

    val expectedYaml =
      """--- !<puppet>
        |resource: "hadoop::namenode"
        |classFilter: "hadoop::namenode"
        |factFilter: ""
      """.stripMargin.trim

    //TODO this is order dependent, but YAML isn't
    yaml shouldBe expectedYaml
  }

  "yaml serialize on bytes on case class should return yaml response in bytes" in {
    val command = PuppetCommand("hadoop::namenode", "hadoop::namenode", "")

    val yaml = command.serialize[BytesYAML]
    logger.info("Command YAML: {}", new String(yaml))

    val expectedYaml =
      """--- !<puppet>
        |resource: "hadoop::namenode"
        |classFilter: "hadoop::namenode"
        |factFilter: ""
      """.stripMargin.trim

    //TODO this is order dependent, but YAML isn't
    yaml shouldBe expectedYaml.getBytes()
  }



  "json serialize on list of case class should return json response" in {
    val command = List[Command](PuppetCommand("hadoop::namenode", "hadoop::namenode", ""), PuppetCommand("hadoop::namenode", "hadoop::namenode", ""))

    val json = command.serialize[StringJSON]
    logger.info("Command JSON: {}", json)

    val expectedJson = """[{"type":"puppet","resource":"hadoop::namenode","classFilter":"hadoop::namenode","factFilter":""},{"type":"puppet","resource":"hadoop::namenode","classFilter":"hadoop::namenode","factFilter":""}]"""

    //TODO this is order dependent, but JSON isn't
    json shouldBe expectedJson
  }

  "json serialize on bytes on list of case class should return json response in bytes" in {
    val command = List[Command](PuppetCommand("hadoop::namenode", "hadoop::namenode", ""), PuppetCommand("hadoop::namenode", "hadoop::namenode", ""))

    val json = command.serialize[BytesJSON]
    logger.info("Command JSON: {}", new String(json))

    val expectedJson = """[{"type":"puppet","resource":"hadoop::namenode","classFilter":"hadoop::namenode","factFilter":""},{"type":"puppet","resource":"hadoop::namenode","classFilter":"hadoop::namenode","factFilter":""}]"""

    //TODO this is order dependent, but JSON isn't
    json shouldBe expectedJson.getBytes()
  }

  "yaml serialize on list of case class should return yaml response" in {
    val command = List(PuppetCommand("hadoop::namenode", "hadoop::namenode", ""), PuppetCommand("hadoop::namenode", "hadoop::namenode", ""))

    val yaml = command.serialize[StringYAML]
    logger.info("Command YAML: {}", yaml)

    val expectedYaml =
      """---
        |- !<puppet>
        |  resource: "hadoop::namenode"
        |  classFilter: "hadoop::namenode"
        |  factFilter: ""
        |- !<puppet>
        |  resource: "hadoop::namenode"
        |  classFilter: "hadoop::namenode"
        |  factFilter: ""
      """.stripMargin.trim

    //TODO this is order dependent, but YAML isn't
    yaml shouldBe expectedYaml
  }

  "yaml serialize on bytes on list of case class should return yaml response in bytes" in {
    val command = List(PuppetCommand("hadoop::namenode", "hadoop::namenode", ""), PuppetCommand("hadoop::namenode", "hadoop::namenode", ""))

    val yaml = command.serialize[BytesYAML]
    logger.info("Command YAML: {}", new String(yaml))

    val expectedYaml =
      """---
        |- !<puppet>
        |  resource: "hadoop::namenode"
        |  classFilter: "hadoop::namenode"
        |  factFilter: ""
        |- !<puppet>
        |  resource: "hadoop::namenode"
        |  classFilter: "hadoop::namenode"
        |  factFilter: ""
      """.stripMargin.trim

    //TODO this is order dependent, but YAML isn't
    yaml shouldBe expectedYaml.getBytes()
  }

  "serializing a object that cant convert to json while using safe should return failure" in {
    object HardTypeForJackson
    val json = HardTypeForJackson.serializeValidation[StringJSON]
    logger.info("Failed JSON {}", json)
    json.isFailure shouldBe true
  }

  "serialize with try" in {
    object HardTypeForJackson
    val json = HardTypeForJackson.serializeTry[StringJSON]
    logger.info("Try JSON {}", json)
    json.isFailure shouldBe true
  }

}
