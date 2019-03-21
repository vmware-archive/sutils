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

import java.lang.annotation.Annotation
import javax.validation.{ConstraintValidator, ConstraintViolation, ConstraintViolationException, Validation => JXValidation}
import com.gopivotal.sutils.Validate
import com.gopivotal.sutils.hibernate.validators.{SizeValidatorForScalaCollection, SizeValidatorForScalaOption}
import org.hibernate.validator.internal.engine.ValidatorFactoryImpl
import org.hibernate.validator.internal.metadata.core.ConstraintHelper

import scala.reflect.ClassTag
import scalaz.{NonEmptyList, Validation}

trait HibernateValidateFunctions {

  import scala.collection.convert.WrapAsScala._
  import scalaz.Scalaz._

  private val validatorFactory = JXValidation.buildDefaultValidatorFactory()

  validatorFactory match {
    case v: ValidatorFactoryImpl =>
      val sizeValidators = List(
        classOf[SizeValidatorForScalaCollection],
        classOf[SizeValidatorForScalaOption])
      // we need to resort to reflection here to get access to the ConstraintHelper, where all the magic happens
      val constraintHelperMethod = classOf[ValidatorFactoryImpl].getDeclaredField("constraintHelper")
      constraintHelperMethod.setAccessible(true)
      val constraintHelper = constraintHelperMethod.get(v).asInstanceOf[ConstraintHelper]

      // add custom constraint mappings
      addValidators[Size](constraintHelper, sizeValidators)
    case _ => // ignore
  }

  private def addValidators[A <: Annotation : ClassTag](helper: ConstraintHelper,
                                                        validators: List[Class[_ <: ConstraintValidator[A, _]]]) {
    import scala.collection.JavaConverters.seqAsJavaListConverter
    import scala.reflect.classTag
    val annoClass = classTag[A].runtimeClass.asInstanceOf[Class[A]]
    val allValidators = new java.util.LinkedList[Class[_ <: ConstraintValidator[A, _]]](validators.asJava)

    // ensure we don't replace existing validators
    allValidators.addAll(helper.getAllValidatorClasses(annoClass))

    helper.putValidatorClasses(annoClass, allValidators, false)
  }

  /**
   * Attempts to validate A, returning javax.validation constraint issues if any are found.  If the Failure case is
   * returned, the list will always have at least one element in it.
   */
  implicit def javaxValidator[A]: Validate[A, Validation[List[ConstraintViolation[_]], A]] =
    new Validate[A, Validation[List[ConstraintViolation[_]], A]] {
      override def validate(a: A): Validation[List[ConstraintViolation[A]], A] = {
        val result = validatorFactory.getValidator.validate(a).toList
        if (result.isEmpty) a.success
        else result.fail
      }
    }

  /**
   * Attempts to validate A, returning javax.validation constraint issues if any are found
   */
  implicit def javaxNelValidator[A]: Validate[A, Validation[NonEmptyList[ConstraintViolation[_]], A]] =
    new Validate[A, Validation[NonEmptyList[ConstraintViolation[_]], A]] {
      override def validate(a: A): Validation[NonEmptyList[ConstraintViolation[A]], A] = {
        val result = validatorFactory.getValidator.validate(a).toList
        if (result.isEmpty) a.success
        else NonEmptyList(result.head, result.tail: _*).fail
      }
    }

  /**
   * Attempts to validate A, returning a javax.validation constraint exception if any violations are found
   */
  implicit def javaxValidatorException[A]: Validate[A, Validation[ConstraintViolationException, A]] =
    new Validate[A, Validation[ConstraintViolationException, A]] {
      override def validate(a: A): Validation[ConstraintViolationException, A] = {
        // cast because scala can't auto convert ? to _
        val result: java.util.Set[ConstraintViolation[_]] = validatorFactory.getValidator.validate(a).asInstanceOf[java.util.Set[ConstraintViolation[_]]]
        if (result.isEmpty) a.success
        else new ConstraintViolationException(result).fail
      }
    }
}

object HibernateValidate extends HibernateValidateFunctions {

  implicit class ToHibernateValidateOpt[A](val self: A) extends AnyVal {
    def validateBean(implicit v: Validate[A, Validation[List[ConstraintViolation[_]], A]]): Validation[List[ConstraintViolation[_]], A] =
      v.validate(self)

    def validateBeanNel(implicit v: Validate[A, Validation[NonEmptyList[ConstraintViolation[_]], A]]): Validation[NonEmptyList[ConstraintViolation[_]], A] =
      v.validate(self)

    def validateBeanException(implicit v: Validate[A, Validation[ConstraintViolationException, A]]): Validation[ConstraintViolationException, A] =
      v.validate(self)
  }

}
