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

import com.fasterxml.jackson.annotation.JsonSubTypes.Type
import com.fasterxml.jackson.annotation.{JsonSubTypes, JsonTypeInfo}
import org.slf4j.LoggerFactory

object DeserializeTest {

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
    new Type(value = classOf[ServiceCommand], name = "service"),
    new Type(value = classOf[PuppetCommand], name = "puppet")
  ))
  trait Command

  case class ServiceCommand(name: String,
                            args: String,
                            classFilter: String,
                            factFilter: String) extends Command

  case class PuppetCommand(resource: String,
                           classFilter: String,
                           factFilter: String) extends Command

}

class DeserializeTest extends BaseTest {

  import DeserializeTest._
  import com.gopivotal.sutils.Format._
  import com.gopivotal.sutils.JacksonDeserialize._
  import com.gopivotal.sutils.JacksonSerialize._
  import com.gopivotal.sutils.Serialize._
  import com.gopivotal.sutils.Deserialize._
  import com.gopivotal.sutils.syntax.deserialize._
  import com.gopivotal.sutils.syntax.serialize._

  val logger = LoggerFactory.getLogger(getClass.getName)

  "convert json string to case class" in {
    val expectedCommand = PuppetCommand("hadoop::namenode", "hadoop::namenode", "")
    val json = expectedCommand.serialize[StringJSON]

    logger.info("JSON {}", json)

    val command = json.deserialize[Command]
    command shouldBe expectedCommand
  }

  "convert json bytes to case class" in {
    val expectedCommand = PuppetCommand("hadoop::namenode", "hadoop::namenode", "")
    val json = expectedCommand.serialize[BytesJSON]

    logger.info("JSON {}", json)

    val command = json.deserialize[PuppetCommand]
    command shouldBe expectedCommand
  }

  "convert json string to list of case class" in {
    val expectedCommand: List[Command] = List(PuppetCommand("hadoop::namenode", "hadoop::namenode", ""), PuppetCommand("hadoop::namenode", "hadoop::namenode", ""))
    val json = expectedCommand.serialize[StringJSON]

    logger.info("JSON {}", json)

    val command = json.deserialize[List[PuppetCommand]]
    command shouldBe expectedCommand
  }

  "convert json bytes to list of case class" in {
    val expectedCommand = List[Command](PuppetCommand("hadoop::namenode", "hadoop::namenode", ""), PuppetCommand("hadoop::namenode", "hadoop::namenode", ""))
    val json = expectedCommand.serialize[BytesJSON]

    logger.info("JSON {}", json)

    val command = json.deserialize[List[PuppetCommand]]
    command shouldBe expectedCommand
  }

  "convert yaml string to case class" in {
    val expectedCommand = PuppetCommand("hadoop::namenode", "hadoop::namenode", "")
    val yaml = expectedCommand.serialize[StringYAML]

    logger.info("JSON {}", yaml)

    val command = yaml.deserialize[PuppetCommand]
    command shouldBe expectedCommand
  }

  "convert yaml bytes to case class" in {
    val expectedCommand = PuppetCommand("hadoop::namenode", "hadoop::namenode", "")
    val yaml = expectedCommand.serialize[BytesYAML]

    logger.info("JSON {}", yaml)

    val command = yaml.deserialize[PuppetCommand]
    command shouldBe expectedCommand
  }

  "convert yaml string to list of case class" in {
    val expectedCommand = List[Command](PuppetCommand("hadoop::namenode", "hadoop::namenode", ""), PuppetCommand("hadoop::namenode", "hadoop::namenode", ""))
    val yaml = expectedCommand.serialize[StringYAML]

    logger.info("JSON {}", yaml)

    val command = yaml.deserialize[List[PuppetCommand]]
    command shouldBe expectedCommand
  }

  "convert yaml bytes to list of case class" in {
    val expectedCommand = List[Command](PuppetCommand("hadoop::namenode", "hadoop::namenode", ""), PuppetCommand("hadoop::namenode", "hadoop::namenode", ""))
    val yaml = expectedCommand.serialize[BytesYAML]

    logger.info("JSON {}", yaml)

    val command = yaml.deserialize[List[PuppetCommand]]
    command shouldBe expectedCommand
  }

  "safe deserializer returns Failure when cant deserialize" in {
    val parsed = JSON("foo").deserializeValidation[PuppetCommand]
    logger.info("Parsed JSON {}", parsed)

    parsed.isFailure shouldBe true
  }

  "safe serde round and round" in {
    val round = PuppetCommand("foo", "bar", "baz").serializeValidation[StringJSON] flatMap (_.deserializeValidation[PuppetCommand])
    logger.info("Round and Round safty: {}", round)
    round.isFailure shouldBe false
  }

  "deserialize with try" in {
    val json = JSON("foo").deserializeTry[PuppetCommand]
    logger.info("Try JSON {}", json)
    json.isFailure shouldBe true
  }

}
