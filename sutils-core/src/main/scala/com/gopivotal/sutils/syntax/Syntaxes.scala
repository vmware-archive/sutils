package com.gopivotal.sutils.syntax

trait Syntaxes {

  object serialize extends ToSerializeOps

  object deserialize extends ToDeserializeOps

  object serde extends ToSerializeOps with ToDeserializeOps

  object validate extends ToValidateOpt

  object all extends ToTypeClassOps
}

trait ToTypeClassOps
  extends ToDeserializeOps with ToSerializeOps with ToValidateOpt