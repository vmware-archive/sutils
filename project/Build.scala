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
package sutils

import sbt.Keys._
import sbt._

object SutilsBuild extends Build {

  // this says it works online, but not seeing it work
  initialCommands in console := """
                                  |import com.gopivotal.sutils.serde._
                                  |import Serialize._
                                  |import Deserialize._
                                  |import Format._
                                  |
                                  |import com.gopivotal.sutils.validate._
                                  |import Hibernate.Annotations._
                                  |import Validate._
                                """.stripMargin


  val sharedSettings = Project.defaultSettings ++ Seq(
    organization := "com.gopivotal",
    scalaVersion := "2.10.4",
    version := "0.1.0",
    crossScalaVersions := Seq("2.10.4", "2.11.1"),
    javacOptions ++= Seq("-source", "1.7", "-target", "1.7"),
    javacOptions in doc := Seq("-source", "1.7"),
    parallelExecution in Test := true,
    scalacOptions ++= Seq(Opts.compile.unchecked, Opts.compile.deprecation, Opts.compile.explaintypes)
  )

  val scalazVersion = "7.0.6"
  val jacksonVersion = "2.4.0"

  lazy val sutils = Project(
    id = "sutils",
    base = file("."),
    settings = sharedSettings
  ).settings(
      test := {},
      publish := {}, // skip publishing for this root project.
      publishLocal := {}
    ).aggregate(core, jacksonSerde, hibernateValidate, examples)
    .dependsOn(core, jacksonSerde, hibernateValidate, examples)

  lazy val core = module("core").settings(
    libraryDependencies += "org.scalaz" % "scalaz-core_2.10" % scalazVersion,

    libraryDependencies += "org.scalatest" % "scalatest_2.10" % "2.2.0" % "test"
  )

  lazy val jacksonSerde = module("jackson").settings(
    libraryDependencies += "com.fasterxml.jackson.core" % "jackson-core" % jacksonVersion,
    libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion,
    libraryDependencies += "com.fasterxml.jackson.dataformat" % "jackson-dataformat-yaml" % jacksonVersion,
    libraryDependencies += "com.fasterxml.jackson.module" % "jackson-module-afterburner" % jacksonVersion,
    libraryDependencies += "com.fasterxml.jackson.module" % "jackson-module-scala_2.10" % "2.3.3", // upgrade when it releases

    libraryDependencies += "org.scalatest" % "scalatest_2.10" % "2.2.0" % "test",
    libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.2" % "test"
  ).dependsOn(core, core % "test->test")

  lazy val hibernateValidate = module("hibernate").settings(
    libraryDependencies += "org.hibernate" % "hibernate-validator" % "5.0.3.Final",
    libraryDependencies += "org.glassfish.web" % "javax.el" % "2.2.5",

    libraryDependencies += "org.scalatest" % "scalatest_2.10" % "2.2.0" % "test",
    libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.2" % "test"
  ).dependsOn(core, core % "test->test")

  lazy val examples = module("examples").dependsOn(
    core,
    jacksonSerde,
    hibernateValidate
  )


  def module(name: String) = {
    val id = "sutils-%s".format(name)
    Project(id = id, base = file(id), settings = sharedSettings ++ Seq(
      Keys.name := id
    ))
  }
}


// vim: set ts=4 sw=4 et:
