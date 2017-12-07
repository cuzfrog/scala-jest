package sjest.conversion

import scala.scalajs.js

private final case class JsTestCase(name: String, runBlock: js.Function) extends JsTestTree

private object JsTestCase {
  implicit val conversion: JsTestConversion[JsTestCase, Seq[String] => String] =
    new JsTestConversion[JsTestCase, Seq[String] => String] {
      override def convert(t: JsTestCase): Seq[String] => String = {
        val convertFromPath = (upperPath: Seq[String]) => {
          val jsPath = "['" + (upperPath :+ t.name).mkString("','") + "']"

          s"""test('${t.name}', () => {
             |  loadTest($jsPath)();
             |});""".stripMargin
        }
        convertFromPath
      }
    }
}
