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
package com.gopivotal.sutils.hibernate.validators

import javax.validation.constraints.Size
import javax.validation.{ConstraintValidator, ConstraintValidatorContext}

import org.hibernate.validator.internal.util.logging.LoggerFactory

import scala.collection.GenTraversableOnce

object SizeValidatorForScalaCollection {
  val log = LoggerFactory.make()
}

class SizeValidatorForScalaCollection extends ConstraintValidator[Size, GenTraversableOnce[_]] {

  import com.gopivotal.sutils.hibernate.validators.SizeValidatorForScalaCollection.log

  var min = 0
  var max = 0

  def initialize(parameters: Size) {
    min = parameters.min()
    max = parameters.max()

    if (min < 0) throw log.getMinCannotBeNegativeException
    if (max < 0) throw log.getMaxCannotBeNegativeException
    if (max < min) throw log.getLengthCannotBeNegativeException
  }

  def isValid(value: GenTraversableOnce[_], context: ConstraintValidatorContext): Boolean = {
    if (value == null) {
      true
    } else {
      val size = value.size
      size >= min && size <= max
    }
  }
}
