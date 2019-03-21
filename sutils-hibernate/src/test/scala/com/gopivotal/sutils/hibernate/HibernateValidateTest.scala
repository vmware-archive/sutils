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
package com.gopivotal.sutils.hibernate

import javax.validation.ConstraintViolation

import com.gopivotal.sutils.{Validate, BaseTest}
import Validate._
import com.gopivotal.sutils.hibernate.HibernateValidate._
import com.gopivotal.sutils.hibernate._

import scalaz.Scalaz._
import scalaz.{Failure, NonEmptyList, Success, Validation}

object HibernateValidateTest {

  case class Orchestration(@NotEmpty module: String,
                           @NotEmpty version: String,
                           description: Option[String],
                           @NotEmpty actions: List[Action],
                           @NotEmpty roles: List[String])

  trait OrchestrationFunctions {
    def validateAction(a: Action): Validation[NonEmptyList[ConstraintViolation[_]], Action] =
      (a.validateBeanNel |@| a.commands.validateBeanNel) { (a, b) => a}

    def validateOrchestration(a: Orchestration): Validation[NonEmptyList[ConstraintViolation[_]], Orchestration] = {
      import Orchestration.actionValidator
      (a.validateBeanNel |@| a.actions.validateBeanNel) { (l, r) => l}
    }
  }

  trait OrchestrationInstances {
    implicit val actionValidator: Validate[Action, Validation[NonEmptyList[ConstraintViolation[_]], Action]] =
      Validate.validate(Orchestration.validateAction)

    implicit val orchestrationValidator: Validate[Orchestration, Validation[NonEmptyList[ConstraintViolation[_]], Orchestration]] =
      Validate.validate(Orchestration.validateOrchestration)
  }

  object Orchestration extends OrchestrationFunctions with OrchestrationInstances {

  }

  case class Action(@NotEmpty name: String,
                    description: Option[String],
                    @NotEmpty commands: List[Command])

  trait Command

  case class ServiceCommand(@NotEmpty name: String,
                            @NotEmpty args: String,
                            classFilter: Option[String],
                            factFilter: Option[String]) extends Command

  case class ResourceCommand(@NotEmpty name: String,
                             @NotEmpty args: String,
                             classFilter: Option[String],
                             factFilter: Option[String]) extends Command

}

class HibernateValidateTest extends BaseTest {

  import com.gopivotal.sutils.hibernate.HibernateValidateTest.Orchestration._
  import com.gopivotal.sutils.hibernate.HibernateValidateTest._

  def simpleError[A](v: Validation[NonEmptyList[ConstraintViolation[_]], A]) =
    v.leftMap(_.map(v => (v.getRootBeanClass.getSimpleName, v.getPropertyPath.toString, v.getMessage)))

  "non valid yaml" in {
    val orc = Orchestration("zookeeper_3x", null, "Zookeeper module".some, List(
      Action(null, none, List(
        ServiceCommand(null, null, none, none),
        ResourceCommand(null, null, none, none)
      ))
    ), null)

    val dataErrors = List(("ServiceCommand", "args", "may not be empty"), ("ResourceCommand", "args", "may not be empty"), ("ServiceCommand", "name", "may not be empty"), ("ResourceCommand", "name", "may not be empty"))

    simpleError(orc.actions.head.commands.validateBeanNel).leftMap(_.sortBy(_._2).toList) shouldBe Failure(dataErrors.sortBy(_._2))

    val actionErrors = List(("Action", "name", "may not be empty")) ::: dataErrors
    simpleError(orc.actions.head.validateBeanNel).leftMap(_.sortBy(_._2).toList) shouldBe Failure(actionErrors.sortBy(_._2))

    val orcErrors = List(("Orchestration", "roles", "may not be empty"), ("Orchestration", "version", "may not be empty")) ::: actionErrors
    simpleError(orc.validateBeanNel).leftMap(_.sortBy(_._2).toList) shouldBe Failure(orcErrors.sortBy(_._2))
  }

  "valid yaml" in {
    val orc = Orchestration("zookeeper_3x", "0.1", "Zookeeper module".some, List(
      Action("foo", none, List(
        ServiceCommand("foo", "bar", none, none),
        ResourceCommand("foo", "bar", none, none)
      ))
    ), List("client", "server"))

    val validated = simpleError(orc.validateBeanNel)

    validated shouldBe Success(orc)
  }

}
