package sjest.conversion

private[conversion] trait JsTestTree

private object JsTestTree {
  implicit val conversion: Convertible[JsTestTree, Seq[String] => String] =
    new Convertible[JsTestTree, Seq[String] => String] {
      override def convert(t: JsTestTree): Seq[String] => String = {
        val convertFromPath = (upperPath: Seq[String]) => t match {
          case group: JsTestGroup => group.toJsTest(upperPath)
          case testCase: JsTestCase => testCase.toJsTest(upperPath)
        }
        convertFromPath
      }
    }
}