package sjest.conversion

import sjest.support.TypeClass

private trait JsTestTree {
  def toJsTest(upperPath: Seq[String] = Seq.empty)
              (implicit ev: JsTestConversion[JsTestTree, Seq[String] => String]): String = {
    ev.convert(this).apply(upperPath)
  }
}

private object JsTestTree {
  implicit val conversion: JsTestConversion[JsTestTree, Seq[String] => String] =
    new JsTestConversion[JsTestTree, Seq[String] => String] {
      override def convert(t: JsTestTree): Seq[String] => String = {
        val convertFromPath = (upperPath: Seq[String]) => t match {
          case group: JsTestGroup => group.toJsTest(upperPath)
          case testCase: JsTestCase => testCase.toJsTest(upperPath)
        }
        convertFromPath
      }
    }
}

@TypeClass
private trait JsTestConversion[-T, +R] {
  def convert(t: T): R
}