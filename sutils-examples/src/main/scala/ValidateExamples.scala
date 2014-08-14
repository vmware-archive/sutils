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
object ValidateExamples extends App {

  import javax.validation.ConstraintViolation
  import com.gopivotal.sutils.Validate
  import Validate._ // intellij keeps trying to remove this, but then the code doesn't work!
  import com.gopivotal.sutils.hibernate.HibernateValidate._
  import com.gopivotal.sutils.hibernate.NotEmpty

  import scalaz.Scalaz._
  import scalaz.{NonEmptyList, Validation}


  // makes the errors more REPL friendly
  def simpleError[A](v: Validation[NonEmptyList[ConstraintViolation[_]], A]) =
    v.leftMap(_.map(v => (v.getRootBeanClass.getSimpleName, v.getPropertyPath.toString, v.getMessage)))


  class ReplHack {

    trait Command

    case class ServiceCommand(@NotEmpty name: String,
                              @NotEmpty args: String,
                              classFilter: Option[String],
                              factFilter: Option[String]) extends Command

    case class ResourceCommand(@NotEmpty name: String,
                               @NotEmpty args: String,
                               classFilter: Option[String],
                               factFilter: Option[String]) extends Command

    case class Action(@NotEmpty name: String,
                      description: Option[String],
                      @NotEmpty commands: List[Command])

    case class Orchestration(@NotEmpty module: String,
                             @NotEmpty version: String,
                             description: Option[String],
                             @NotEmpty actions: List[Action],
                             @NotEmpty roles: List[String])

    trait OrchestrationFunctions {

      def validateAction(a: Action): Validation[NonEmptyList[ConstraintViolation[_]], Action] =
        (a.validateBeanNel |@| a.commands.validateBeanNel) { (a, b) => a}

      def validateOrchestration(a: Orchestration): Validation[NonEmptyList[ConstraintViolation[_]], Orchestration] = {
        import Orchestration.actionValidator // intellij, stop removing this!
        (a.validateBeanNel |@| a.actions.validateBeanNel) { (l, r) => l}
      }
    }

    trait OrchestrationInstances {

      implicit val actionValidator: Validate[Action, Validation[NonEmptyList[ConstraintViolation[_]], Action]] =
        Validate.validate(Orchestration.validateAction)

      implicit val orchestrationValidator: Validate[Orchestration, Validation[NonEmptyList[ConstraintViolation[_]], Orchestration]] =
        Validate.validate(Orchestration.validateOrchestration)
    }

    object Orchestration extends OrchestrationFunctions with OrchestrationInstances

  }

  val hack = new ReplHack

  import ValidateExamples.hack._
  import Orchestration._

  val orc = Orchestration(null, null, None, List(
    Action(null, None, List()),
    Action(null, None, List(
      ServiceCommand(null, null, None, None),
      ResourceCommand(null, null, None, None)
    ))
  ), null)

  println(simpleError(orc.actions.head.commands.validateBeanNel))
  // Success(List())

  println(simpleError(orc.actions.tail.head.commands.validateBeanNel))
  // Failure(List((ServiceCommand,args,may not be empty), (ServiceCommand,name,may not be empty), (ResourceCommand,args,may not be empty), (ResourceCommand,name,may not be empty)))

  println(simpleError(orc.actions.head.validateBeanNel))
  // Failure(NonEmptyList((Action,commands,may not be empty), (Action,name,may not be empty)))

  println(simpleError(orc.validateBeanNel))
  // Failure(NonEmptyList((Orchestration,version,may not be empty), (Orchestration,module,may not be empty), (Orchestration,roles,may not be empty), (Action,name,may not be empty), (Action,commands,may not be empty), (Action,name,may not be empty), (ServiceCommand,name,may not be empty), (ServiceCommand,args,may not be empty), (ResourceCommand,name,may not be empty), (ResourceCommand,args,may not be empty)))
}
