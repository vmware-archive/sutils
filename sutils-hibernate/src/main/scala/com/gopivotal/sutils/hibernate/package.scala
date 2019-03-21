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

import javax.validation.{constraints => jsr}

import org.hibernate.validator.{constraints => hiber}

import scala.annotation.meta.{field, param}

package object hibernate {
  // JSR-303 constraints
  type AssertFalse = jsr.AssertFalse@field @param
  type AssertTrue = jsr.AssertTrue@field @param
  type DecimalMax = jsr.DecimalMax@field @param
  type DecimalMin = jsr.DecimalMin@field @param
  type Digits = jsr.Digits@field @param
  type Future = jsr.Future@field @param
  type Max = jsr.Max@field @param
  type Min = jsr.Min@field @param
  type NotNull = jsr.NotNull@field @param
  type Null = jsr.Null@field @param
  type Past = jsr.Past@field @param
  type Pattern = jsr.Pattern@field @param
  type Size = jsr.Size@field @param
  type Valid = javax.validation.Valid@field @param

  // extra Hibernate Validator constraints
  type ConstraintComposition = hiber.ConstraintComposition
  type CreditCardNumber = hiber.CreditCardNumber@field @param
  type Email = hiber.Email@field @param
  type Length = hiber.Length@field @param
  type ModCheck = hiber.ModCheck@field @param
  type NotBlank = hiber.NotBlank@field @param
  type NotEmpty = hiber.NotEmpty@field @param
  type Range = hiber.Range@field @param
  type SafeHtml = hiber.SafeHtml@field @param
  type ScriptAssert = hiber.ScriptAssert
  type URL = hiber.URL@field @param
}
