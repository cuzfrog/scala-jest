package sjest.conversion

import sjest.support.TypeClass

private trait JsTestTree {
  def toJsTest(implicit ev: JsTestConversion[JsTestTree, String]): String = ev.convert(this)
}

@TypeClass
private trait JsTestConversion[T, R] {
  def convert(t: T): R
}