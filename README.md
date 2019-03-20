# Sutils

Sutils stands for scala utilis, and as such its a collection of libraries for scala development.

## Packages

This section will go over the different packages and give examples for how to use them

### serde

Serde is short for Serialize/Deserialize.  This package contains typeclasses for serde operations.

```scala
import com.gopivotal.sutils.serde.{Serialize, Deserialize}
import com.gopivotal.sutils.serde.jackson.{JacksonSerialize, JacksonDeserialize, Format}
import Serialize._
import Deserialize._
import Format._
import JacksonSerialize._
import JacksonDeserialize._

case class Foo[A](name: String, value: A)

val root = Foo("root", List(
  Foo("left", None),
  Foo("right", List(
    Foo("leaf", 1)
  ))
))

val json = root.serialize[StringJSON]
// {"name":"root","value":[{"name":"left","value":null},{"name":"right","value":[{"name":"leaf","value":1}]}]}

json.deserializeValidation[Foo[_]]
// Success(Foo(root,List(Map(name -> left, value -> null), Map(name -> right, value -> List(Map(name -> leaf, value -> 1))))))

json.deserializeValidation[Foo[List[Foo[List[Foo[Int]]]]]]
// Success(Foo(root,List(Foo(left,null), Foo(right,List(Foo(leaf,1))))))
```

The above defaults to using [Jackson](https://jackson.codehaus.org/) for serde operations, but could be switched to anything that defines a implicit function that returns `Serialize[A, B]` (same for deserialze).

### validate

Validate contains a typeclass called `Validate` which takes a `A` and tries to see if it is logically sound (fields are not null, input is in the expected range, etc.).

```scala
import javax.validation.ConstraintViolation

  import com.gopivotal.sutils.validate.Validate
  import com.gopivotal.sutils.hibernate.{HibernateValidate, NotEmpty}
  import Validate._
  import HibernateValidate._

  import scalaz.Scalaz._
  import scalaz.{Failure, NonEmptyList, Success, Validation}


  // makes the errors more REPL friendly
  def simpleError[A](v: Validation[NonEmptyList[ConstraintViolation[_]], A]) =
    v.leftMap(_.map(v => (v.getRootBeanClass.getSimpleName, v.getPropertyPath.toString, v.getMessage)))

  // in REPL, the getClass.getSimpleName method freaks out, so wrap all classes in a hack class to get around this
  class REPLHack {

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

    object Orchestration extends OrchestrationFunctions with OrchestrationInstances

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

  val hack = new REPLHack

  import ValidateExamples.hack._
  import Orchestration._

  val orc = Orchestration(null, null, None, List(
                                                  Action(null, None, List()),
                                                  Action(null, None, List(
                                                                           ServiceCommand(null, null, None, None),
                                                                           ResourceCommand(null, null, None, None)
                                                                         ))
                                                ), null)

  simpleError(orc.actions.head.commands.validateBeanNel)
  // Success(List())

  simpleError(orc.actions.tail.head.commands.validateBeanNel)
  // Failure(List((ServiceCommand,args,may not be empty), (ServiceCommand,name,may not be empty), (ResourceCommand,args,may not be empty), (ResourceCommand,name,may not be empty)))

  orc.actions.head.validateBeanNel)
  // Failure(NonEmptyList((Action,commands,may not be empty), (Action,name,may not be empty)))

  simpleError(orc.validateBeanNel)
  // Failure(NonEmptyList((Orchestration,version,may not be empty), (Orchestration,module,may not be empty), (Orchestration,roles,may not be empty), (Action,name,may not be empty), (Action,commands,may not be empty), (Action,name,may not be empty), (ServiceCommand,name,may not be empty), (ServiceCommand,args,may not be empty), (ResourceCommand,name,may not be empty), (ResourceCommand,args,may not be empty)))
```

This example looks overly complex, but its showing the harder case for validate.  The default implementation uses [Hibernate Validator](https://hibernate.org/validator/) which doesn't support nested validation checks for scala.  If your object needs nested validation, you can follow the code above.  [Scalaz](https://github.com/scalaz/scalaz)'s `Validation` type will make sure all errors join together for the response
