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
object SerdeExamples extends App {

  import com.gopivotal.sutils._
  import Deserialize._
  import Serialize._
  import Format._
  import JacksonDeserialize._
  import JacksonSerialize._
  import syntax.deserialize._
  import syntax.serialize._

  case class Foo[A](name: String, value: A)

  val root = Foo("root", List(
    Foo("left", None),
    Foo("right", List(
      Foo("leaf", 1)
    ))
  ))

  val json = root.serialize[StringJSON]
  println(json)
  // {"name":"root","value":[{"name":"left","value":null},{"name":"right","value":[{"name":"leaf","value":1}]}]}

  println(json.deserializeValidation[Foo[_]])
  // Success(Foo(root,List(Map(name -> left, value -> null), Map(name -> right, value -> List(Map(name -> leaf, value -> 1))))))

  println(json.deserializeValidation[Foo[List[Foo[List[Foo[Int]]]]]])
  // Success(Foo(root,List(Foo(left,null), Foo(right,List(Foo(leaf,1))))))
}
